package com.jar.app.feature.account

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarHome
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.orZero
import com.jar.app.core_analytics.EventKey
import com.jar.app.databinding.FragmentAccountBinding
import com.jar.app.event.AccountFragmentPositionChangedEvent
import com.jar.app.feature_profile.api.ProfileApi
import com.jar.app.feature_profile.shared.MR
import com.jar.app.feature_settings.api.SettingsApi
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@AndroidEntryPoint
internal class AccountFragment : BaseFragment<FragmentAccountBinding>() {

    companion object {
        fun newInstance() = AccountFragment()
    }

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var settingsApi: SettingsApi

    @Inject
    lateinit var profileApi: ProfileApi

    private var pagerAdapter: AccountPagerAdapter? = null

    private var mediator: TabLayoutMediator? = null

    private var handler: Handler? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAccountBinding
        get() = FragmentAccountBinding::inflate

    override fun setupAppBar() {

    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
    }

    private fun setupUI() {
        pagerAdapter = AccountPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle, settingsApi, profileApi)
        binding.viewPager.adapter = pagerAdapter
        mediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getCustomString(requireContext(), MR.strings.feature_profile_profile)
                1 -> getCustomString(requireContext(), MR.strings.feature_profile_settings)
                else -> throw Exception("Invalid tab position")
            }
        }
        mediator?.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position.orZero() == 1) {
                    analyticsHandler.postEvent(EventKey.Clicked_SettingsTab_profileScreen)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault()
            .post(UpdateAppBarEvent(AppBarData(ToolbarHome(showStoryIcon = false, showNotification = true))))
    }

    override fun onDestroyView() {
        mediator?.detach()
        mediator = null
        binding.tabLayout.clearOnTabSelectedListeners()
        binding.viewPager.adapter = null
        pagerAdapter = null
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler(Looper.getMainLooper())
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler?.removeCallbacksAndMessages(null)
        handler = null
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onAccountFragmentPositionChangedEvent(accountFragmentPositionChangedEvent: AccountFragmentPositionChangedEvent) {
        EventBus.getDefault().removeStickyEvent(accountFragmentPositionChangedEvent)
        handler?.postDelayed({
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED))
                try {
                    if (isBindingInitialized())
                        binding?.viewPager?.setCurrentItem(
                            accountFragmentPositionChangedEvent.position,
                            true
                        )
                } catch (exception: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(exception)
                }
        }, 200)
    }

}
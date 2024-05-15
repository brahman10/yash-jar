package com.jar.app.feature_gold_lease.impl.ui.landing

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.dp
import com.jar.app.base.util.getSecondAndMillisecondFormat
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants.GoldLeaseTabPosition.TAB_MY_ORDERS
import com.jar.app.core_base.util.BaseConstants.GoldLeaseTabPosition.TAB_NEW_LEASE
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_gold_lease.GoldLeaseNavigationDirections
import com.jar.app.feature_gold_lease.R
import com.jar.app.feature_gold_lease.databinding.FragmentGoldLeaseLandingBinding
import com.jar.app.feature_gold_lease.impl.ui.my_orders.GoldLeaseV2MyOrdersFragment
import com.jar.app.feature_gold_lease.impl.ui.new_lease_page.GoldLeaseV2NewLeaseFragment
import com.jar.app.feature_gold_lease.shared.util.GoldLeaseEventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@AndroidEntryPoint
internal class GoldLeaseLandingFragment : BaseFragment<FragmentGoldLeaseLandingBinding>() {

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private var adapter: GoldLeaseLandingAdapter? = null

    private val args by navArgs<GoldLeaseLandingFragmentArgs>()

    private var isNewLeaseUserFromEvent: Boolean? = null

    private var counter = 0

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGoldLeaseLandingBinding
        get() = FragmentGoldLeaseLandingBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarNone
                )
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
    }

    private fun setupListeners() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                analyticsApi.postEvent(
                    GoldLeaseEventKey.GoldLeaseNewLeaseScreen.Lease_InfoScreenClicked,
                    mapOf(
                        GoldLeaseEventKey.Properties.USER_TYPE to if (isNewLeaseUser()) GoldLeaseEventKey.Values.NEW else GoldLeaseEventKey.Values.REPEAT,
                        GoldLeaseEventKey.Properties.BUTTON_TYPE to if (binding.tabLayout.getTabAt(0) == tab) GoldLeaseEventKey.Values.TAB_NEW_LEASE else GoldLeaseEventKey.Values.TAB_MY_ORDERS,
                        GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType
                    )
                )
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }

    private fun setupUI() {
        setupToolbar()
        binding.tabLayout.isVisible = isNewLeaseUser().not()

        val fragments: MutableList<Fragment> = ArrayList()
        fragments.add(GoldLeaseV2NewLeaseFragment.newInstance(args.flowType, isNewLeaseUser()))
        if (isNewLeaseUser().not()) fragments.add(GoldLeaseV2MyOrdersFragment.newInstance(args.flowType))

        adapter =
            GoldLeaseLandingAdapter(
                childFragmentManager,
                viewLifecycleOwner.lifecycle,
                fragments
            )
        binding.landingViewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.landingViewPager) { tab, position ->
            tab.text = when (position) {
                TAB_NEW_LEASE -> getString(R.string.feature_gold_lease_new_leases)
                TAB_MY_ORDERS -> getString(R.string.feature_gold_lease_my_orders)
                else -> throw Exception("Invalid tab position in Gold Lease")
            }
        }.attach()

        binding.landingViewPager.setCurrentItem(args.tabPosition, false)
        val currentTime = System.currentTimeMillis()
        if (counter == 0) {
            counter += 1
            analyticsApi.postEvent(
                GoldLeaseEventKey.GoldLeaseNewLeaseScreen.Lease_InfoScreenShown_Ts,
                mapOf(
                    EventKey.TIME_IT_TOOK to getSecondAndMillisecondFormat(endTimeTime = currentTime, startTime = args.clickTime.toLong())
                )
            )
        }
    }

    private fun setupToolbar()  {
        binding.toolbar.root.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(), com.jar.app.core_ui.R.color.bgColor
            )
        )
        binding.toolbar.tvTitle.isVisible = true
        binding.toolbar.lottieView.isVisible = false
        binding.toolbar.ivEndImage.isVisible = false

        binding.toolbar.separator.isVisible = true

        binding.toolbar.tvTitle.text = getString(R.string.feature_gold_lease_gold_lease)

        //Setup FAQ Button
        binding.toolbar.tvEnd.setBackgroundResource(com.jar.app.core_ui.R.drawable.bg_rounded_40_121127)
        binding.toolbar.tvEnd.setPadding(16.dp,8.dp,16.dp,8.dp)
        binding.toolbar.tvEnd.setTextColor(
            ContextCompat.getColor(
                requireContext(), com.jar.app.core_ui.R.color.color_EEEAFF
            )
        )
        binding.toolbar.tvEnd.text = getString(R.string.feature_gold_lease_faqs)
        binding.toolbar.tvEnd.isVisible = true

        binding.toolbar.tvEnd.setDebounceClickListener {
            analyticsApi.postEvent(
                GoldLeaseEventKey.CommonEvents.Lease_FAQButtonClicked,
                mapOf(
                    GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType,
                    GoldLeaseEventKey.Properties.SCREEN_NAME to if (binding.landingViewPager.currentItem == 0) GoldLeaseEventKey.Screens.NEW_LEASES_SCREEN else GoldLeaseEventKey.Screens.MY_ORDER_SCREEN
                )
            )
            navigateTo(
                GoldLeaseNavigationDirections.actionToGoldLeaseFaqBottomSheetFragment(
                    flowType = args.flowType
                )
            )
        }

        binding.toolbar.btnBack.setDebounceClickListener {
            analyticsApi.postEvent(
                GoldLeaseEventKey.CommonEvents.Lease_BackButtonClicked,
                mapOf(
                    GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType,
                    GoldLeaseEventKey.Properties.SCREEN_NAME to if (binding.landingViewPager.currentItem == 0) GoldLeaseEventKey.Screens.NEW_LEASES_SCREEN else GoldLeaseEventKey.Screens.MY_ORDER_SCREEN
                )
            )
            popBackStack()
        }
    }

    private fun isNewLeaseUser() = isNewLeaseUserFromEvent ?: args.isNewLeaseUser

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshLeaseLandingScreenEvent(refreshLandingScreenEvent: com.jar.app.feature_gold_lease.shared.domain.event.RefreshLeaseLandingScreenEvent) {
        isNewLeaseUserFromEvent = refreshLandingScreenEvent.isNewLeaseUser
    }

    override fun onDestroyView() {
        adapter = null
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}
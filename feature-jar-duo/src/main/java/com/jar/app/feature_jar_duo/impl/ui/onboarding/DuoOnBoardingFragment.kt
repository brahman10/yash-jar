package com.jar.app.feature_jar_duo.impl.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.decodeUrl
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_jar_duo.R
import com.jar.app.feature_jar_duo.databinding.FeatureDuoFragmentOnboardingBinding
import com.jar.app.feature_jar_duo.shared.domain.model.OnboardingData
import com.jar.app.feature_jar_duo.shared.util.DuoConstants
import com.jar.app.feature_jar_duo.shared.util.DuoEventKey
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject


@AndroidEntryPoint
internal class DuoOnBoardingFragment : BaseFragment<FeatureDuoFragmentOnboardingBinding>() {
    /*

   *//* @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi*//*

    private val viewModel by viewModels<DuoOnBoardingViewModel> {
        defaultViewModelProviderFactory
    }

    private var invitesStatus: Boolean = false

    private var duoGroupsListStatus: Boolean = false

    private val args by navArgs<DuoOnBoardingFragmentArgs>()

    private val fromScreen by lazy {
        decodeUrl(args.fromScreen)
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDuoFragmentOnboardingBinding
        get() = FeatureDuoFragmentOnboardingBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
    }

    fun setupUI() {
        analyticsHandler.postEvent(DuoEventKey.Shown_Screen_Duo, DuoEventKey.introduction)
        viewModel.fetchPendingInvites()
        viewModel.fetchGroupList()
        observeLiveData()

        val dataList = listOf(
            OnboardingData(
                "${BaseConstants.CDN_BASE_URL}${DuoConstants.ImageEndpoints.FACE_MULTIPLE}",
                getString(R.string.feature_duo_introduncing_jar_duo),
                getString(R.string.feature_duo_buddy_up_and_build_a_savings_habit_together)
            ),
            OnboardingData(
                "${BaseConstants.CDN_BASE_URL}${DuoConstants.ImageEndpoints.FACE_MULTIPLE}",
                getString(R.string.feature_duo_invite_your_friend_start_a_duo),
                null
            ),
            OnboardingData(
                "${BaseConstants.CDN_BASE_URL}${DuoConstants.ImageEndpoints.FACE_MULTIPLE}",
                getString(R.string.feature_duo_build_savings_habit_together),
                null
            )
        )
        binding.viewPager.adapter = DuoAdapterOnboarding(
            dataList,
            childFragmentManager,
            viewLifecycleOwner.lifecycle
        )
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.btnGetStarted.isVisible =
                    position == 2 && fromScreen == DuoConstants.SOURCE_HOME
            }
        })

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position -> }.attach()
    }

    fun setupListeners() {

        binding.btnClose.setDebounceClickListener {
            popBackStack()
        }

        binding.btnGetStarted.setDebounceClickListener {
            prefs.setShowOnBoardingScreenForDuo(false)
            if (invitesStatus or duoGroupsListStatus) {
                navigateTo(
                    DuoOnBoardingFragmentDirections.actionOnBoardingFragmentToDuosList()
                )
            } else {
                navigateTo(
                    DuoOnBoardingFragmentDirections.actionOnBoardingFragmentToCreateFirstDuoFragment()
                )
            }
        }
    }

    fun observeLiveData() {
        viewModel.listGetPendingInvitesLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                invitesStatus = it.isNotEmpty()
            }
        )
        viewModel.listGroupsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                duoGroupsListStatus = it.isNotEmpty()
            }
        )

    }
}*/
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDuoFragmentOnboardingBinding
        get() = FeatureDuoFragmentOnboardingBinding::inflate

    override fun setupAppBar() {

    }

    override fun setup(savedInstanceState: Bundle?) {
        Glide.with(this).load(BaseConstants.ImageUrlConstants.FEATURE_DUO_BG_DUO_SPIRAL).into(binding.ivSpiralBg)
    }
}
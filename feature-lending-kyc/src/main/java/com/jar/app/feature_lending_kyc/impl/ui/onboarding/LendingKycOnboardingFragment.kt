package com.jar.app.feature_lending_kyc.impl.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycOnboardingFragmentBinding
import com.jar.app.feature_lending_kyc.impl.ui.steps.LendingKycStepsFragmentArgs
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class LendingKycOnboardingFragment :
    BaseFragment<FeatureLendingKycOnboardingFragmentBinding>() {

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycOnboardingFragmentBinding
        get() = FeatureLendingKycOnboardingFragmentBinding::inflate

    private var activityRef: WeakReference<FragmentActivity>? = null

    private val args: LendingKycStepsFragmentArgs by navArgs()

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    companion object {
        private const val GET_STARTED = "Get Started"
        private const val BACK_ARROW = "Back Arrow"
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setStatusBarColor(com.jar.app.core_ui.R.color.bgColor)
        getData()
        setupUI()
        observeLiveData()
        setupListeners()
    }

    private fun getData() {
        analyticsHandler.postEvent(LendingKycEventKey.Shown_KYCLandingScreen)
    }

    private fun setupUI() {
        activityRef = WeakReference(requireActivity())
        Glide.with(requireContext())
            .load(BaseConstants.CDN_BASE_URL + LendingKycConstants.IllustrationUrls.LENDING_KYC_ONBOARDING_NEW_URL)
            .into(binding.ivHeaderIllustration)
        binding.toolbar.tvTitle.text = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_verification)
    }

    private fun setupListeners() {
        binding.btnGetStarted.setDebounceClickListener {
            analyticsHandler.postEvent(
                LendingKycEventKey.Clicked_Button_KYCLandingScreen,
                mapOf(LendingKycEventKey.fromScreen to GET_STARTED)
            )
            navigateTo(
                LendingKycOnboardingFragmentDirections.actionToLendingKycStepsFragment(args.flowType),
                popUpTo = R.id.lendingKycOnboardingFragment,
                inclusive = true,
                shouldAnimate = true
            )
        }

        binding.toolbar.btnBack.setDebounceClickListener {
            analyticsHandler.postEvent(
                LendingKycEventKey.Clicked_Button_KYCLandingScreen,
                mapOf(LendingKycEventKey.fromScreen to BACK_ARROW)
            )
            popBackStack()
        }
    }

    private fun observeLiveData() {

    }

}
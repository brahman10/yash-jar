package com.jar.app.feature_lending_kyc.impl.ui.success_screens

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.LendingAadharVerificationDoneEvent
import com.jar.app.base.data.event.LendingKycCompletedEvent
import com.jar.app.base.data.event.LendingKycCompletedEventV2
import com.jar.app.base.data.event.LendingRedirectionType
import com.jar.app.base.data.event.PanVerificationDoneEvent
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.base.util.isPresentInBackStack
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycDialogStepSuccessBinding
import com.jar.app.feature_lending_kyc.impl.ui.steps.LendingKycStepsViewModel
import com.jar.app.core_base.data.dto.getKycFeatureFlowType
import com.jar.app.core_base.data.dto.isFromLending
import com.jar.app.core_base.data.dto.isFromP2POrLending
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.app.feature_lending_kyc.shared.util.LendingKycFlowType
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class SuccessStepDialog : BaseDialogFragment<FeatureLendingKycDialogStepSuccessBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args: SuccessStepDialogArgs by navArgs()

    private val lendingKycStepsViewModel: LendingKycStepsViewModel by activityViewModels()

    private var animationJob: Job? = null
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycDialogStepSuccessBinding
        get() = FeatureLendingKycDialogStepSuccessBinding::inflate

    override val dialogConfig: DialogFragmentConfig
        get() = DEFAULT_CONFIG

    override fun setup() {
        setupUI()
    }

    private fun setupUI() {
        binding.otpSentAnimation.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.SMALL_CHECK
        )
        binding.otpSentAnimation.playAnimation()
        setTitleByFlowType(args.flowType)
        lendingKycStepsViewModel.fetchKycProgress(
            WeakReference(requireActivity()), false, shouldNavigate = false
        )
        uiScope.launch {
            delay(1000)
            when (args.flowType) {
                LendingKycFlowType.EMAIL -> {
                    analyticsHandler.postEvent(LendingKycEventKey.Shown_EmailVerificationSuccessfulScreen)
                    navigateTo(
                        FeatureLendingKycStepsNavigationDirections.actionToCreditReportOtpVerificationLoadingDialog(),
                        shouldAnimate = true,
                        popUpTo = R.id.successStepDialog,
                        inclusive = true
                    )
                }

                LendingKycFlowType.PAN -> {
                    analyticsHandler.postEvent(
                        LendingKycEventKey.Shown_PANVerificationSuccessfulScreen,
                        mapOf(
                            LendingKycEventKey.fromScreen to args.fromScreen,
                            LendingKycEventKey.lenderName to args.lenderName.orEmpty()
                        )
                    )
                    if (getKycFeatureFlowType(args.kycFeatureFlowType).isFromP2POrLending()) {
                        val popupTo =
                            if (findNavController().isPresentInBackStack(R.id.creditReportNotAvailableFragment))
                                R.id.creditReportNotAvailableFragment
                            else if (findNavController().isPresentInBackStack(R.id.enterPanManuallyFragment))
                                R.id.enterPanManuallyFragment
                            else R.id.creditReportFetchedFragment
                        popBackStack(popupTo, true)
                        EventBus.getDefault().postSticky(PanVerificationDoneEvent(args.fromScreen))
                    } else {
                        navigateTo(
                            FeatureLendingKycStepsNavigationDirections.actionToAadhaarCkycfetchFragment(),
                            shouldAnimate = true,
                            popUpTo = R.id.successStepDialog,
                            inclusive = true
                        )
                    }
                }

                LendingKycFlowType.AADHAAR -> {
                    analyticsHandler.postEvent(
                        if (getKycFeatureFlowType(args.kycFeatureFlowType).isFromLending()) LendingKycEventKey.Lending_AadharManualVerificationSuccessful
                        else LendingKycEventKey.Shown_AadhaarVerificationSuccessfulScreen,
                        mapOf(
                            LendingKycEventKey.fromScreen to args.fromScreen,
                            LendingKycEventKey.lenderName to args.lenderName.orEmpty()
                        )
                    )
                    if (getKycFeatureFlowType(args.kycFeatureFlowType).isFromP2POrLending()) {
                        popBackStack(R.id.KYCOptionFragment, true)
                        EventBus.getDefault().postSticky(LendingAadharVerificationDoneEvent())
                    }
                }

                LendingKycFlowType.SELFIE -> {
                    analyticsHandler.postEvent(
                        if (getKycFeatureFlowType(args.kycFeatureFlowType).isFromP2POrLending()) LendingKycEventKey.Lending_SelfieCheckSuccessfulScreenShown
                        else LendingKycEventKey.Shown_SelfieVerificationSuccessfulScreen,
                        mapOf(
                            LendingKycEventKey.fromScreen to args.fromScreen,
                            LendingKycEventKey.lenderName to args.lenderName.orEmpty()
                        )
                    )
                    showSuccessAnimation()
                }
            }
        }
    }

    private fun showSuccessAnimation() {
        animationJob = viewLifecycleOwner.lifecycleScope.launch {
//            binding.ivKycLottie.playLottieWithUrlAndExceptionHandling(
//                requireContext(),
//                BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.TICK_WITH_CELEBRATION
//            )
//            binding.confettiLottie.playAnimation()
//            delay(2000)
            analyticsHandler.postEvent(
                LendingKycEventKey.Shown_LendingKycVerificationSuccessfulScreen,
                mapOf(
                    LendingKycEventKey.fromScreen to args.fromScreen,
                    LendingKycEventKey.lenderName to args.lenderName.orEmpty()
                )
            )
            if (getKycFeatureFlowType(args.kycFeatureFlowType).isFromP2POrLending()) {
                val popupTo =
                    if (findNavController().isPresentInBackStack(R.id.aadhaarManualEntryFragment))
                        R.id.aadhaarManualEntryFragment
                    else R.id.selfieCheckFragment
                popBackStack(popupTo, true)
                EventBus.getDefault().postSticky(LendingKycCompletedEventV2())
            } else {
                if (lendingKycStepsViewModel.flowType == BaseConstants.LendingKycFromScreen.LENDING_CARD)
                    EventBus.getDefault()
                        .post(LendingKycCompletedEvent(BaseConstants.MICRO_LOAN_DETAILS_URL))
                if (lendingKycStepsViewModel.flowType == BaseConstants.LendingKycFromScreen.LENDING_ONBOARDING)
                    EventBus.getDefault().post(
                        LendingKycCompletedEvent(
                            "",
                            LendingRedirectionType.TYPE_REDIRECTION_LENDING_INAPP
                        )
                    )
                else
                    EventBus.getDefault()
                        .post(
                            GoToHomeEvent(
                                getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_your_selfie_verification_is_successful),
                                BaseConstants.HomeBottomNavigationScreen.HOME
                            )
                        )
            }
        }
    }

    private fun setTitleByFlowType(lendingLendingKycFlowType: LendingKycFlowType) {
        when (lendingLendingKycFlowType) {
            LendingKycFlowType.EMAIL -> {
                binding.tvSuccessTitle.text =
                    getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_your_email_verification_is_successful)
            }

            LendingKycFlowType.PAN -> {
                binding.tvSuccessTitle.text =
                    getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_your_pan_verification_is_successful)
            }

            LendingKycFlowType.AADHAAR -> {
                binding.tvSuccessTitle.text =
                    getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_your_aadhaar_verification_is_successful)
            }

            LendingKycFlowType.SELFIE -> {
                binding.tvSuccessTitle.text =
                    getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_your_selfie_verification_is_successful)
            }
        }
    }


    override fun onDestroyView() {
        animationJob?.cancel()
        super.onDestroyView()
    }
}
package com.jar.app.feature_lending_kyc.impl.ui.pan.loading_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.core_base.data.dto.getKycFeatureFlowType
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycDialogVerifyPanDetailsBinding
import com.jar.app.feature_lending_kyc.impl.ui.pan.error_screens.PanErrorStatesArguments
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenPrimaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenSecondaryButtonAction
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.app.feature_lending_kyc.shared.util.LendingKycFlowType
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
internal class VerifyPanDetailsDialog :
    BaseDialogFragment<FeatureLendingKycDialogVerifyPanDetailsBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycDialogVerifyPanDetailsBinding
        get() = FeatureLendingKycDialogVerifyPanDetailsBinding::inflate
    override val dialogConfig: DialogFragmentConfig
        get() = DEFAULT_CONFIG

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    companion object {
        const val PAN_VERIFYING_DETAILS_SCREEN = "PAN Verifying Details Screen"
    }

    private val args: VerifyPanDetailsDialogArgs by navArgs()

    private val viewModelProvider: VerifyPanDetailsViewModelAndroid by viewModels()
    private val viewModel by lazy { viewModelProvider.getInstance() }


    override fun setup() {
        setupUI()
        observeFlow()
    }

    private fun setupUI() {
        analyticsHandler.postEvent(
            LendingKycEventKey.Shown_PANVerifyingDetailsScreen,
            mapOf(LendingKycEventKey.fromScreen to args.fromScreen)
        )
        binding.lottie.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.VERIFYING
        )
        viewModel.verifyPanDetails(
            args.creditReportPan,
            getKycFeatureFlowType(args.kycFeatureFlowType)
        )
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.verifyPanDetailsFlow.collect(
                    onLoading = {
                    },
                    onSuccess = {
                        uiScope.launch {
                            delay(1000)
                            navigateToNextScreen()
                        }
                    },
                    onSuccessWithNullData = {
                        uiScope.launch {
                            delay(1000)
                            navigateToNextScreen()
                        }
                    },
                    onError = { message, errorCode ->
                        when (errorCode) {
                            BaseConstants.ErrorCodesLendingKyc.PAN.OTP_ATTEMPT_LIMIT_EXCEEDED -> {
                                navigateTo(
                                    FeatureLendingKycStepsNavigationDirections.actionToPanErrorStatesFragment(
                                        PanErrorStatesArguments(
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan_entry_attempt_limit_exceeded),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_please_try_again_tomorrow),
                                            BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                                            PanErrorScreenPrimaryButtonAction.GO_HOME,
                                            PanErrorScreenSecondaryButtonAction.CONTACT_SUPPORT,
                                            contactMessage = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_credit_report_otp_limit_exceeded),
                                            kycFeatureFlowType = getKycFeatureFlowType(args.kycFeatureFlowType)
                                        )
                                    ),
                                    popUpTo = R.id.verifyPanDetailsDialog,
                                    inclusive = true
                                )
                            }

                            BaseConstants.ErrorCodesLendingKyc.PAN.OTP_ATTEMPT_LIMIT_EXHAUSTED -> {
                                navigateTo(
                                    FeatureLendingKycStepsNavigationDirections.actionToPanErrorStatesFragment(
                                        PanErrorStatesArguments(
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan_entry_attempt_limit_exhausted),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_please_contact_customer_support),
                                            BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                                            PanErrorScreenPrimaryButtonAction.GO_HOME,
                                            PanErrorScreenSecondaryButtonAction.CONTACT_SUPPORT,
                                            contactMessage = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_credit_report_otp_limit_exhausted),
                                            kycFeatureFlowType = getKycFeatureFlowType(args.kycFeatureFlowType)
                                        )
                                    ),
                                    popUpTo = R.id.verifyPanDetailsDialog,
                                    inclusive = true
                                )
                            }

                            BaseConstants.ErrorCodesLendingKyc.PAN.NSDL_VERIFICATION_FAILED -> {
                                navigateTo(
                                    FeatureLendingKycStepsNavigationDirections.actionToPanErrorStatesFragment(
                                        PanErrorStatesArguments(
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_hmm_this_is_taking_some_time),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_please_contact_customer_support),
                                            BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                                            primaryAction = if (args.jarVerifiedPAN.orFalse()) PanErrorScreenPrimaryButtonAction.USE_PAN_SAVED_WITH_JAR else PanErrorScreenPrimaryButtonAction.ENTER_PAN_MANUALLY,
                                            secondaryAction = if (args.jarVerifiedPAN.orFalse()) PanErrorScreenSecondaryButtonAction.ENTER_PAN_MANUALLY else PanErrorScreenSecondaryButtonAction.NONE,
                                            jarVerifiedPAN = args.jarVerifiedPAN,
                                            contactMessage = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan_details_could_not_be_verified),
                                            kycFeatureFlowType = getKycFeatureFlowType(args.kycFeatureFlowType)
                                        )
                                    ),
                                    popUpTo = R.id.verifyPanDetailsDialog,
                                    inclusive = true
                                )
                            }

                            BaseConstants.ErrorCodesLendingKyc.PAN.NSDL_VERIFICATION_ATTEMPT_LIMIT_EXCEEDED -> {
                                navigateTo(
                                    FeatureLendingKycStepsNavigationDirections.actionToPanErrorStatesFragment(
                                        PanErrorStatesArguments(
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_uh_oh_exclamation),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan_entry_limit_exceeded_please_come_back_tomorrow),
                                            BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                                            PanErrorScreenPrimaryButtonAction.GO_HOME,
                                            PanErrorScreenSecondaryButtonAction.CONTACT_SUPPORT,
                                            contactMessage = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan_details_could_not_be_verified),
                                            kycFeatureFlowType = getKycFeatureFlowType(args.kycFeatureFlowType)
                                        )
                                    ),
                                    popUpTo = R.id.verifyPanDetailsDialog,
                                    inclusive = true
                                )
                            }

                            BaseConstants.ErrorCode.SOME_ERROR_OCCURRED_PLEASE_TRY_AGAIN -> {
                                navigateTo(
                                    FeatureLendingKycStepsNavigationDirections.actionToPanErrorStatesFragment(
                                        PanErrorStatesArguments(
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_uh_oh_exclamation),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_your_pan_details_could_not_be_verified),
                                            BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                                            primaryAction = if (args.jarVerifiedPAN.orFalse()) PanErrorScreenPrimaryButtonAction.USE_PAN_SAVED_WITH_JAR else PanErrorScreenPrimaryButtonAction.ENTER_PAN_MANUALLY,
                                            secondaryAction = if (args.jarVerifiedPAN.orFalse()) PanErrorScreenSecondaryButtonAction.ENTER_PAN_MANUALLY else PanErrorScreenSecondaryButtonAction.NONE,
                                            jarVerifiedPAN = args.jarVerifiedPAN,
                                            contactMessage = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_verification_failed),
                                            kycFeatureFlowType = getKycFeatureFlowType(args.kycFeatureFlowType)
                                        )
                                    ),
                                    popUpTo = R.id.otpVerificationFragment,
                                    inclusive = true
                                )
                            }

                            else -> {
                                message.snackBar(binding.root)
                                uiScope.launch {
                                    delay(1000)
                                    dismiss()
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    private fun navigateToNextScreen() {
        navigateTo(
            FeatureLendingKycStepsNavigationDirections.actionToSuccessStepDialog(
                flowType = LendingKycFlowType.PAN,
                fromScreen = PAN_VERIFYING_DETAILS_SCREEN,
                lenderName = null,
                kycFeatureFlowType = args.kycFeatureFlowType
            ),
            shouldAnimate = true,
            popUpTo = R.id.verifyPanDetailsDialog,
            inclusive = true
        )
    }
}
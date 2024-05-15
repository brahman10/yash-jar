package com.jar.app.feature_lending_kyc.impl.ui.pan.report_fetched.loading

import com.jar.app.base.ui.fragment.BaseDialogFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.makeColoredLink
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.slideToRevealNew
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycFragmentCreditReportOtpVerificationLoadingBinding
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class CreditReportOtpVerificationLoadingDialog :
    BaseDialogFragment<FeatureLendingKycFragmentCreditReportOtpVerificationLoadingBinding>() {

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycFragmentCreditReportOtpVerificationLoadingBinding
        get() = FeatureLendingKycFragmentCreditReportOtpVerificationLoadingBinding::inflate

    override val dialogConfig: DialogFragmentConfig
        get() = DialogFragmentConfig()

    private val phoneNumber by lazy {
        val masked = StringBuilder()
        masked.append(prefs.getUserPhoneNumber()?.take(3).orEmpty())
        masked.append(" ***** ")
        masked.append(prefs.getUserPhoneNumber()?.takeLast(5).orEmpty())
        masked.toString()
    }

    private var validityInSeconds: Long = 0
    private var resentOTPInSeconds: Long = 0
    private var isApiCallCompleted = false
    private var isLoadingSimulationCompleted = false

    private val viewModelProvider: CreditReportOtpVerificationLoadingViewModelAndroid by viewModels()
    private val viewModel by lazy { viewModelProvider.getInstance() }


    override fun setup() {
        setupUI()
        observeFlow()
    }

    private fun setupUI() {
        viewModel.requestCreditReportOtp()
        simulateLoading()
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.requestCreditReportOtpFlow.collect(
                    onLoading = {},
                    onSuccess = {
                        it?.let {
                            validityInSeconds = it.validityInSeconds.orZero()
                            resentOTPInSeconds = it.resentOTPInSeconds.orZero()
                            isApiCallCompleted = true
                            if (isLoadingSimulationCompleted) {
                                redirectToPanVerificationToOpenOtpSheet(
                                    validityInSeconds,
                                    resentOTPInSeconds
                                )
                            }
                        }
                    },
                    onError = { message, errorCode ->
                        dismissProgressBar()
                        when (errorCode) {
                            BaseConstants.ErrorCodesLendingKyc.PAN.OTP_ATTEMPT_LIMIT_EXCEEDED -> {
                                navigateTo(
                                    FeatureLendingKycStepsNavigationDirections.actionToCreditReportOtpResendLimitExceededBottomSheet(
                                        getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_come_back_later),
                                        getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_please_try_again_tomorrow),
                                        false,
                                        isComeBackLaterFlow = true
                                    ), true
                                )
                            }

                            BaseConstants.ErrorCodesLendingKyc.PAN.OTP_ATTEMPT_LIMIT_EXHAUSTED -> {
                                navigateTo(
                                    FeatureLendingKycStepsNavigationDirections.actionToCreditReportOtpResendLimitExceededBottomSheet(
                                        getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_otp_resned_limit_exceeded),
                                        getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_please_enter_your_pan_details),
                                        false,
                                        isComeBackLaterFlow = false
                                    ), true
                                )
                            }
                        }
                    }
                )
            }
        }
    }

    private fun simulateLoading() {
        uiScope.launch {
            analyticsHandler.postEvent(LendingKycEventKey.Shown_LookingForCreditReportScreen)
            binding.tvSendingOtp.text =
                getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_sending_otp_on_your_mobile)
            binding.tvPoweredBy.isVisible = true
            delay(1000L)
            binding.tvSendingOtpDesc.isVisible = true
            val desc = getCustomStringFormatted(
                com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_help_us_fetch_your_credit_report_for_s,
                phoneNumber
            )
            binding.tvSendingOtpDesc.makeColoredLink(
                message = desc,
                words = listOf(phoneNumber),
                color = ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.white),
                shouldUnderlineWords = false,
                {}
            )
            binding.tvThisWillNotAffectYourCreditScore.isVisible = true
            delay(2500L)
            binding.tvThisWillNotAffectYourCreditScore.isVisible = false
            binding.clLoadingDataContainer.slideToRevealNew(
                binding.clOtpSent,
                onAnimationEnd = {
                    binding.otpSentAnimation.playLottieWithUrlAndExceptionHandling(
                        requireContext(),
                        BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.SMALL_CHECK
                    )
                    binding.tvTitle.text = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_otp_sent)
                }
            )
            delay(1500)
            isLoadingSimulationCompleted = true
            if (isApiCallCompleted) {
                redirectToPanVerificationToOpenOtpSheet(validityInSeconds, resentOTPInSeconds)
            }
        }
    }

    private fun redirectToPanVerificationToOpenOtpSheet(expiresInTime: Long, resendTime: Long) {
        navigateTo(
            FeatureLendingKycStepsNavigationDirections.actionToPanVerificationFragment(
                LendingKycConstants.PanFlowType.LENDING_FLOW.name,
                expiresInTime,
                resendTime
            ),
            popUpTo = R.id.creditReportOtpVerificationLoadingDialog,
            inclusive = true,
            shouldAnimate = true
        )
    }

}
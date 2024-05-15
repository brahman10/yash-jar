package com.jar.app.feature_lending_kyc.impl.ui.pan.base_pan_verification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.makeColoredLink
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycFragmentPanVerificationBinding
import com.jar.app.feature_lending_kyc.impl.data.Step
import com.jar.app.feature_lending_kyc.impl.domain.event.ToolbarStepsVisibilityEvent
import com.jar.app.feature_lending_kyc.impl.ui.otp.OtpSheetArguments
import com.jar.app.feature_lending_kyc.impl.ui.steps.LendingKycStepsViewModel
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.app.feature_lending_kyc.shared.util.LendingKycFlowType
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class PanVerificationFragment :
    BaseFragment<FeatureLendingKycFragmentPanVerificationBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycFragmentPanVerificationBinding
        get() = FeatureLendingKycFragmentPanVerificationBinding::inflate

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    private val args: PanVerificationFragmentArgs by navArgs()

    private val viewModelProvider: PanVerificationViewModelAndroid by viewModels()
    private val viewModel by lazy { viewModelProvider.getInstance() }


    private var panFlowType: LendingKycConstants.PanFlowType? = null
    private val lendingKycStepsViewModel: LendingKycStepsViewModel by activityViewModels()

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                lendingKycStepsViewModel.viewOnlyNavigationRedirectTo(
                    LendingKycFlowType.PAN,
                    false,
                    WeakReference(requireActivity())
                )
            }
        }

    override fun setupAppBar() {
        EventBus.getDefault().post(ToolbarStepsVisibilityEvent(shouldShowSteps = true, Step.PAN))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeFlow()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        panFlowType = LendingKycConstants.PanFlowType.valueOf(args.flowType)
        val masked = StringBuilder()
        masked.append(prefs.getUserPhoneNumber()?.take(3).orEmpty())
        masked.append(" ***** ")
        masked.append(prefs.getUserPhoneNumber()?.takeLast(5).orEmpty())

        val title = getCustomStringFormatted(
            com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_help_us_fetch_your_credit_report_for_s,
            masked.toString()
        )
        binding.tvPanOtpSent.makeColoredLink(
            message = title,
            words = listOf(masked.toString()),
            color = ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.white),
            shouldUnderlineWords = false,
            {}
        )
        Glide.with(requireContext())
            .load(BaseConstants.CDN_BASE_URL + LendingKycConstants.IllustrationUrls.PAN_PLACEHOLDER_URL)
            .into(binding.ivIllustration)
        when (panFlowType) {
            LendingKycConstants.PanFlowType.LENDING_FLOW -> {
                redirectToOtpVerification(args.expiresInTime, args.resendTime)
            }
            LendingKycConstants.PanFlowType.CREDIT_REPORT,
            LendingKycConstants.PanFlowType.JAR_VERIFIED,
            LendingKycConstants.PanFlowType.MANUAL,
            LendingKycConstants.PanFlowType.BACK_FLOW,
            LendingKycConstants.PanFlowType.IMAGE -> {
            }
            null -> {}
        }

    }

    private fun setupListeners() {
        binding.btnGetOtp.setDebounceClickListener {
            viewModel.requestCreditReportOtp()
        }

        binding.ivQuestionMark.setDebounceClickListener {
            navigateTo(
                FeatureLendingKycStepsNavigationDirections.actionToLendingKycFaqBottomSheet()
            )
        }
    }

    private fun observeFlow() =
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.requestCreditReportOtpFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        redirectToOtpVerification(
                            it?.validityInSeconds.orZero(), it?.resentOTPInSeconds.orZero()
                        )
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
                                    )
                                )
                            }

                            BaseConstants.ErrorCodesLendingKyc.PAN.OTP_ATTEMPT_LIMIT_EXHAUSTED -> {
                                navigateTo(
                                    FeatureLendingKycStepsNavigationDirections.actionToCreditReportOtpResendLimitExceededBottomSheet(
                                        getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_otp_resned_limit_exceeded),
                                        getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_please_enter_your_pan_details),
                                        false,
                                        isComeBackLaterFlow = false
                                    )
                                )
                            }
                        }
                    }
                )
            }
        }

    private fun redirectToOtpVerification(expiresInTime: Long, resendTime: Long) {
        panFlowType = LendingKycConstants.PanFlowType.CREDIT_REPORT
        val args = encodeUrl(
            serializer.encodeToString(
                OtpSheetArguments(
                    flowType = LendingKycConstants.LendingKycOtpVerificationFlowType.CREDIT_REPORT,
                    expiresInTime = expiresInTime,
                    resendTime = resendTime,
                    fromScreen = LendingKycConstants.PanFlowType.CREDIT_REPORT.name
                )
            )
        )
        navigateTo(
            FeatureLendingKycStepsNavigationDirections.actionToOtpVerificationFragment(
                args
            ),
            shouldAnimate = true
        )
    }


    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }
}
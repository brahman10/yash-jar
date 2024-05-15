package com.jar.app.feature_lending.impl.ui.otp

import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.OTPReceivedEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.countDownTimer
import com.jar.app.base.util.getMaskedString
import com.jar.app.base.util.getOtp
import com.jar.app.base.util.milliSecondsToCountDown
import com.jar.app.base.util.openWhatsapp
import com.jar.app.base.util.setOnImeActionDoneListener
import com.jar.app.base.util.textChanges
import com.jar.app.core_base.domain.model.User
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.databinding.FragmentOtpVerificationBinding
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@AndroidEntryPoint
internal class LoanOtpVerificationFragment :
    BaseBottomSheetDialogFragment<FragmentOtpVerificationBinding>() {

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private var resendOtpJob: Job? = null
    private var otpExpireJob: Job? = null

    private val args by navArgs<LoanOtpVerificationFragmentArgs>()

    private val viewModelProvider: OtpViewModelAndroid by viewModels { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentOtpVerificationBinding
        get() = FragmentOtpVerificationBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = DEFAULT_CONFIG.copy(isHideable = false, isCancellable = false, isDraggable = false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsApi.postEvent(
            event = LendingEventKeyV2.Lending_LoanAgreementScreenShown,
            values = mapOf(
                LendingEventKeyV2.action to LendingEventKeyV2.enter_otp_screen_show
            )
        )
    }

    override fun setup() {
        setupUI()
        setupListener()
        observeFlow()
        getData()
    }

    override fun getTheme(): Int {
        return com.jar.app.core_ui.R.style.BottomSheetDialogInput
    }

    private fun setupUI() {
        toggleMainButton(true)
        uiScope.launch {
            val maskedNumber =
                prefs.getUserString()?.let {
                    serializer.decodeFromString<User>(it)
                }?.phoneNumber?.getMaskedString(4, 8, "X")
            val span = buildSpannedString {
                append(getCustomString(MR.strings.feature_lending_sent_to_your_registered_mobile_number))
                color(ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.white)) {
                    append(" $maskedNumber ")
                }
            }.toSpannable()
            binding.tvNumber.text =
                "$span ${
                    getCustomString(
                        if (args.isFromRepeatWithdrawal)
                            MR.strings.feature_lending_to_confirm_request
                        else MR.strings.feature_lending_to_e_sign_agreement
                    )
                }"
        }
    }

    private fun setupListener() {
        binding.ivClose.setDebounceClickListener {
            findNavController().navigateUp()
            setFragmentResult(
                LendingConstants.OtpVerificationRequest.OTP_VERIFICATION_REQUEST_KEY,
                bundleOf(
                    Pair(
                        LendingConstants.OtpVerificationRequest.OTP_VERIFICATION_REQUEST_RESULT,
                        viewModel.currentValidationState
                    )
                )
            )
        }

        binding.tvResendOtp.setDebounceClickListener {
            if (viewModel.otpTimeLeft <= 0) {
                viewModel.sendOtp(args.loanId, getCheckPointType())
            }
        }

        binding.btnAction.setDebounceClickListener {
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Lending_LoanAgreementScreenShown,
                values = mapOf(
                    LendingEventKeyV2.action to LendingEventKeyV2.verify_otp_clicked
                )
            )
            viewModel.verifyOtp(
                args.loanId,
                binding.otpView.text?.toString() ?: "",
                getCheckPointType()
            )
        }

        binding.otpView.textChanges()
            .debounce(300)
            .onEach {
                toggleMainButton()
                if (binding.tvOtpError.isVisible)
                    binding.tvOtpError.isVisible = false
            }.launchIn(uiScope)

        binding.otpView.setOnImeActionDoneListener {
            binding.btnAction.performClick()
        }
    }

    private fun getCheckPointType() =
        if (args.isFromRepeatWithdrawal) LendingConstants.LendingApplicationCheckpoints.WITHDRAWAL else LendingConstants.LendingApplicationCheckpoints.LOAN_AGREEMENT

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.otpFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        onOtpSent(it?.validityInSeconds.orZero())
                    },
                    onError = { errorMessage, errorCode ->
                        analyticsApi.postEvent(
                            event = LendingEventKeyV2.Lending_LoanAgreementError,
                            values = mapOf(
                                LendingEventKeyV2.error_type to errorMessage
                            )
                        )
                        dismissProgressBar()
                        if (errorCode == LendingConstants.ErrorCodesLending.LendingOtp.OTP_SEND_LIMIT_EXHAUSTED)
                            showLimitExhaustedLayout()
                        else {
                            binding.tvOtpError.text = errorMessage
                            binding.tvOtpError.isVisible = true
                        }
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.verifyOtpFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        navigateAhead()
                    },
                    onSuccessWithNullData = {
                        navigateAhead()
                    },
                    onError = { errorMsg, errorCode ->
                        dismissProgressBar()
                        if (errorCode == LendingConstants.WITHDRAWAL_ERROR_CODE) {
                            navigateAhead(LendingConstants.OtpVerificationRequest.OTP_VERIFICATION_WITHDRAWAL_ERROR)
                        } else {
                            binding.tvOtpError.text = errorMsg
                            binding.tvOtpError.isVisible = true
                        }
                    }
                )
            }
        }
    }

    private fun getData() {
        viewModel.sendOtp(args.loanId, getCheckPointType())
    }

    private fun onOtpSent(validityInSeconds: Int) {
        dismissProgressBar()
        startResendOtpTimer()
    }

    private fun startResendOtpTimer() {
        resendOtpJob?.cancel()
        resendOtpJob = uiScope.countDownTimer(
            20_000,
            onInterval = {
                viewModel.otpTimeLeft = it.toInt()
                binding.tvResendOtp.text = getResendOtpString(it)
            },
            onFinished = {
                viewModel.otpTimeLeft = 0
                binding.tvResendOtp.text = getResendOtpString(0)
            }
        )
    }

    private fun getResendOtpString(millisLeft: Long): Spannable {
        val spannableBuilder = buildSpannedString {
            if (millisLeft > 0) {
                val t1 =
                    getCustomString(MR.strings.featiure_lending_resend_otp).plus(" ")
                        .plus(getCustomString(MR.strings.featiure_lending_in)).plus(" ")
                val t2 = millisLeft.milliSecondsToCountDown()
                append(t1)
                append(t2)
            } else {
                append(getCustomString(MR.strings.featiure_lending_resend_otp))
            }
        }
        val spannable = spannableBuilder.toSpannable()
        val start = if (millisLeft > 0) spannable.lastIndexOf(" ") else 0
        spannable
            .setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        requireContext(),
                        com.jar.app.core_ui.R.color.white
                    )
                ),
                start,
                spannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        return spannable
    }

    private fun getOtpExpireString(millisLeft: Long): Spannable {
        val spannableBuilder = buildSpannedString {
            if (millisLeft > 0) {
                val t1 =
                    getCustomString(MR.strings.featiure_lending_otp_expires).plus(" ")
                        .plus(getCustomString(MR.strings.featiure_lending_in)).plus(" ")
                val t2 = millisLeft.milliSecondsToCountDown()
                append(t1)
                append(t2)
            } else {
                append(getCustomString(MR.strings.featiure_lending_otp_has_expired))
            }
        }
        val spannable = spannableBuilder.toSpannable()
        val start = if (millisLeft > 0) spannable.lastIndexOf(" ") else 0
        spannable
            .setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        requireContext(),
                        if (millisLeft > 0) com.jar.app.core_ui.R.color.white else com.jar.app.core_ui.R.color.color_ebb46a
                    )
                ),
                start,
                spannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        return spannable
    }

    private fun toggleMainButton(disableAnyway: Boolean = false) {
        val shouldEnable = if (disableAnyway) false
        else
            (viewModel.otpFlow.asLiveData().value?.data?.data != null && ((binding.otpView.text?.length
                ?: 0) == 6))
        binding.btnAction.setDisabled(shouldEnable.not())
        binding.btnAction.isEnabled = shouldEnable
    }

    private fun showLimitExhaustedLayout() {
        viewModel.currentValidationState =
            LendingConstants.OtpVerificationRequest.OTP_VERIFICATION_REQUEST_EXHAUSTED
        binding.successGroup.isVisible = false
        binding.tvOtpError.isVisible = false
        binding.limitExhaustedGroup.isVisible = true
        binding.lottieView.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            LendingConstants.LottieUrls.GENERIC_ERROR
        )
        binding.btnAction.setText(getCustomString(MR.strings.feature_lending_contact_support))
        binding.btnAction.setDrawableStart(com.jar.app.core_ui.R.drawable.ic_whatsapp)
        binding.btnAction.setDisabled(false)
        binding.btnAction.setDebounceClickListener {
            prefs.getUserStringSync()?.let {
                val user = serializer.decodeFromString<User?>(it)
                val message = getCustomStringFormatted(
                    MR.strings.feature_lending_otp_error_wa_message,
                    user?.getFullName().orEmpty(),
                    user?.phoneNumber.orEmpty()
                )
                requireContext().openWhatsapp(remoteConfigApi.getWhatsappNumber(), message)
            }
        }
    }

    private fun navigateAhead(result: String = LendingConstants.OtpVerificationRequest.OTP_VERIFICATION_REQUEST_SUCCESS) {
        dismissProgressBar()
        findNavController().navigateUp()
        setFragmentResult(
            LendingConstants.OtpVerificationRequest.OTP_VERIFICATION_REQUEST_KEY,
            bundleOf(
                Pair(
                    LendingConstants.OtpVerificationRequest.OTP_VERIFICATION_REQUEST_RESULT,
                    result
                )
            )
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onOTPReceivedEvent(otpReceivedEvent: OTPReceivedEvent) {
        EventBus.getDefault().removeStickyEvent(otpReceivedEvent)
        val otp = otpReceivedEvent.originalMessage.getOtp(6) ?: ""
        binding.otpView.setText(otp)
        viewModel.verifyOtp(
            args.loanId,
            otp,
            getCheckPointType()
        )
    }
}
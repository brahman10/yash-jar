package com.jar.app.feature_profile.impl.ui.profile.number

import android.content.IntentFilter
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task
import com.jar.app.base.data.event.OTPReceivedEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.data.receiver.OTPSmsBroadcastReceiver
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.hideKeyboard
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_utils.data.NetworkFlow
import com.jar.app.feature_profile.databinding.FragmentEditProfileVerifyOtpBinding
import com.jar.app.feature_profile.domain.ProfileEventKey
import com.jar.app.feature_profile.shared.MR
import com.jar.app.feature_profile.ui.EditProfileNumberViewModel
import com.jar.app.feature_user_api.domain.model.RequestOtpData
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
import org.threeten.bp.Duration
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileVerifyOtpFragment : BaseFragment<FragmentEditProfileVerifyOtpBinding>(), BaseResources {

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var networkFlow: NetworkFlow

    private var isNetworkAvailable = false

    private val viewModelProvider by viewModels<EditProfileNumberViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val args by navArgs<EditProfileVerifyOtpFragmentArgs>()

    private val otpSmsBroadcastReceiver by lazy { OTPSmsBroadcastReceiver() }

    private var requestOtpData: RequestOtpData? = null

    private var timerJob: Job? = null

    private var otpTimeLeft = -1L
    private var callTimeLeft = -1L

    private var smsDetected = false

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentEditProfileVerifyOtpBinding
        get() = FragmentEditProfileVerifyOtpBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        initClickListeners()
        observeLiveData()
        analyticsHandler.postEvent(ProfileEventKey.Events.Shown_OTPScreen_PhoneNumberPopUp)
    }

    private fun setupUI() {
        requestOtpData = args.requestOtpData
        toggleMainButton(true)
        setOtpCallSpannable()
    }

    private fun initClickListeners() {
        binding.btnVerify.setDebounceClickListener {
            it.context.hideKeyboard(it)
            val otp = binding.otpView.text?.toString()
            if (!otp.isNullOrBlank() && requestOtpData != null) {
                viewModel.verifyNumber(
                    args.phoneNumber.phoneNumber,
                    args.phoneNumber.countryCode,
                    otp,
                    requestOtpData?.reqId!!
                )
            } else {
                getCustomString(MR.strings.feature_profile_please_enter_a_valid_otp).snackBar(binding.root)
            }
        }

        binding.tvResendOtp.setDebounceClickListener {
            if (otpTimeLeft > 0)
                showTooltip(
                    TOOLTIP_TYPE_OTP,
                    getCustomString(MR.strings.feature_profile_you_can_request_otp_after) + " " + otpTimeLeft.milliSecondsToCountDown()
                )
            else {
                showTooltip(TOOLTIP_TYPE_NONE)
                requestOtp()
                setOtpCallSpannable()
            }
        }

        binding.tvCallTimer.setDebounceClickListener {
            if (callTimeLeft > 0)
                showTooltip(
                    TOOLTIP_TYPE_CALL,
                    getCustomString(MR.strings.feature_profile_you_can_verify_via_call_after) + " " + callTimeLeft.milliSecondsToCountDown()
                )
            else {
                showTooltip(TOOLTIP_TYPE_NONE)
                requestOtpViaCall()
                setOtpCallSpannable(false)
            }
        }

        binding.otpView.textChanges()
            .debounce(300)
            .onEach {
                toggleMainButton()
                showTooltip(TOOLTIP_TYPE_NONE)
                if (binding.tvOtpError.isVisible)
                    toggleOtpErrorLayout(false)
            }.launchIn(uiScope)

        binding.tvHelp.setDebounceClickListener {
            val msg = getCustomStringFormatted(MR.strings.feature_profile_my_number_is_x_otp, args.phoneNumber.countryCode + args.phoneNumber.phoneNumber)
            requireContext().openWhatsapp(remoteConfigApi.getWhatsappNumber(), msg)
        }

        binding.otpView.setOnImeActionDoneListener {
            binding.btnVerify.performClick()
        }

        binding.otpView.setDebounceClickListener {
            analyticsHandler.postEvent(
                ProfileEventKey.Events.Clicked_EnterOTP_PhoneNumberPopUp, mapOf(
                    ProfileEventKey.Props.SmsDetected to smsDetected
                )
            )
        }

        binding.btnVerify?.setOnTouchListener { v: View, _ ->
            analyticsHandler.postEvent(
                ProfileEventKey.Events.Clicked_Verify_PhoneNumberPopUp, mapOf(
                    ProfileEventKey.Props.Enabled to binding.btnVerify.isEnabled
                )
            )
            false
        }

        binding.btnBack.setDebounceClickListener {
            requireActivity().onBackPressed() //This is intentional.. new onboarding has special back press handling.. Don't replace with popBackStack..
        }

        view?.setOnTouchListener { v: View, _ ->
            v.hideKeyboard()
            showTooltip(TOOLTIP_TYPE_NONE)
            false
        }
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)
        networkFlow.networkStatus
            .onEach {
                isNetworkAvailable = it
                toggleMainButton()
            }
            .launchIn(uiScope)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.requestOtpLiveData.collect(
                    onSuccess = {
                        startTimer()
                        requestOtpData = it
                    },
                    onError = { errorMessage, errorCode ->
                        toggleOtpErrorLayout(
                            true,
                            if (errorMessage.isBlank()) getCustomString(MR.strings.something_went_wrong) else errorMessage
                        )
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.verifyNumberLiveData.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        navigateTo(
                            EditProfileVerifyOtpFragmentDirections.actionEditProfileVerifyOtpFragmentToEditProfileNumberSuccessFragment(
                                getCustomString(MR.strings.feature_profile_you_have_successfully_changed_you_number),
                                getCustomString(MR.strings.feature_profile_please_login_with_the_new_number)
                            )
                        )
                    },
                    onError = { errorMessage, errorCode ->
                        dismissProgressBar()
                    }
                )
            }
        }

    }

    private fun toggleMainButton(disableAnyway: Boolean = false) {
        val shouldEnable = if (disableAnyway) false else (
                isNetworkAvailable && ((binding.otpView.text?.length ?: 0) == (requestOtpData?.length ?: 6)))
        binding.btnVerify.isEnabled = shouldEnable
    }

    private fun setOtpCallSpannable(isOtp: Boolean = true) {
        val spannable = buildSpannedString {
            if (isOtp)
                append(getCustomString(MR.strings.feature_profile_we_ve_sent_to))
            else
                append(getCustomString(MR.strings.feature_profile_you_will_receive_call))
            append(" ")
            bold {
                append("+")
                append(args.phoneNumber.countryCode.plus(args.phoneNumber.phoneNumber))
            }
        }.toSpannable()
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.color_EEEAFF)),
            spannable.indexOf("+"),
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvDescription.text = spannable
    }

    private fun toggleOtpErrorLayout(isError: Boolean, errorMsg: String = "") {
        binding.otpView.setLineColor(
            ContextCompat.getColor(
                requireContext(),
                if (isError) com.jar.app.core_ui.R.color.color_EB6A6E else com.jar.app.core_ui.R.color.white
            )
        )
        if (isError) {
            binding.tvOtpError.text = errorMsg
            analyticsHandler.postEvent(
                ProfileEventKey.Events.Shown_ErrorMessage_PhoneNumberPopUp, mapOf(
                    ProfileEventKey.Props.ErrorMsg to errorMsg
                )
            )
        }
        binding.tvOtpError.isVisible = isError
    }

    private fun showTooltip(tooltipType: Int, msg: String = "") {
        binding.tooltipOtp.isVisible = tooltipType == TOOLTIP_TYPE_OTP
        binding.tooltipCall.isVisible = tooltipType == TOOLTIP_TYPE_CALL
        when (tooltipType) {
            TOOLTIP_TYPE_OTP -> binding.tooltipOtp.text = msg
            TOOLTIP_TYPE_CALL -> binding.tooltipCall.text = msg
        }
    }

    private fun requestOtp() {
        viewModel.requestOtp(args.phoneNumber.phoneNumber, args.phoneNumber.countryCode)
    }

    private fun requestOtpViaCall() {
        viewModel.requestOtpViaCall(args.phoneNumber.phoneNumber, args.phoneNumber.countryCode)
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = uiScope.countDownTimer(
            Duration.ofSeconds(20).toMillis(),
            onInterval = {
                otpTimeLeft = it
                callTimeLeft = it
                updateTooltipsTime(it)
                binding.tvResendOtp.text = getResendOtpString(it)
                binding.tvCallTimer.text = getCallToVerifyString(it)
            },
            onFinished = {
                otpTimeLeft = -1L
                callTimeLeft = -1L
                binding.tvResendOtp.text = getResendOtpString(0)
                binding.tvCallTimer.text = getCallToVerifyString(0)
                if (binding.otpView.text.isNullOrBlank())
                    showTooltip(TOOLTIP_TYPE_OTP, getCustomString(MR.strings.feature_profile_looks_like_no_otp_enterd))
                else
                    showTooltip(TOOLTIP_TYPE_NONE)
            }
        )
    }

    private fun updateTooltipsTime(time: Long) {
        if (binding.tooltipOtp.isVisible) {
            val msg =
                getCustomStringFormatted(MR.strings.feature_profile_you_can_request_otp_after) + " " + time.milliSecondsToCountDown()
            showTooltip(TOOLTIP_TYPE_OTP, msg)
        }
        if (binding.tooltipCall.isVisible) {
            val msg =
                getCustomStringFormatted(MR.strings.feature_profile_you_can_verify_via_call_after) + " " + time.milliSecondsToCountDown()
            showTooltip(TOOLTIP_TYPE_CALL, msg)
        }
    }

    private fun getResendOtpString(millisLeft: Long): Spannable {
        val spannableBuilder = buildSpannedString {
            if (millisLeft > 0) {
                val t1 =
                    getCustomString(MR.strings.feature_profile_resend_otp).plus(" ").plus(getCustomString(MR.strings.feature_profile_in)).plus(" ")
                val t2 = millisLeft.milliSecondsToCountDown()
                append(t1)
                append(t2)
            } else {
                append(getCustomString(MR.strings.feature_profile_resend_otp))
            }
        }
        val spannable = spannableBuilder.toSpannable()
        val start = if (millisLeft > 0) spannable.lastIndexOf(" ") else 0
        spannable
            .setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.white)),
                start,
                spannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        return spannable
    }

    private fun getCallToVerifyString(millisLeft: Long): Spannable {
        val spannableBuilder = buildSpannedString {
            if (millisLeft > 0) {
                val t1 = getCustomString(MR.strings.feature_profile_call_to_verify).plus(" ").plus(getCustomString(MR.strings.feature_profile_in))
                    .plus(" ")
                val t2 = millisLeft.milliSecondsToCountDown()
                append(t1)
                append(t2)
            } else {
                append(getCustomString(MR.strings.feature_profile_call_to_verify))
            }
        }
        val spannable = spannableBuilder.toSpannable()
        val start = if (millisLeft > 0) spannable.lastIndexOf(" ") else 0
        spannable
            .setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.white)),
                start,
                spannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        return spannable
    }

    private fun startSMSRetrieverClient() {
        val task1 = SmsRetriever.getClient(requireContext()).startSmsUserConsent(args.phoneNumber.phoneNumber)
        task1.addOnSuccessListener { it }
        activity?.registerReceiver(
            otpSmsBroadcastReceiver,
            IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        )
        val client = SmsRetriever.getClient(requireContext())
        val task: Task<Void> = client.startSmsRetriever()
        task.addOnSuccessListener {}
        task.addOnFailureListener {}
    }

    override fun onStart() {
        super.onStart()
        startSMSRetrieverClient()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        activity?.unregisterReceiver(otpSmsBroadcastReceiver)
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onOTPReceivedEvent(otpReceivedEvent: OTPReceivedEvent) {
        EventBus.getDefault().removeStickyEvent(otpReceivedEvent)
        smsDetected = true
        val otp = otpReceivedEvent.originalMessage.getOtp(requestOtpData?.length) ?: ""
        binding.otpView.setText(otp)
        toggleMainButton()
    }

    companion object {
        const val TOOLTIP_TYPE_OTP = 1
        const val TOOLTIP_TYPE_CALL = 2
        const val TOOLTIP_TYPE_NONE = 3
    }
}
package com.jar.app.feature_lending_kyc.impl.ui.otp

import android.content.IntentFilter
import android.graphics.Paint
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.isDigitsOnly
import androidx.core.text.toSpannable
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task
import com.jar.app.base.data.event.OTPReceivedEvent
import com.jar.app.base.data.event.OnNewMessageEvent
import com.jar.app.base.data.receiver.OTPSmsBroadcastReceiver
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.countDownTimer
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.getOtp
import com.jar.app.base.util.milliSecondsToCountDown
import com.jar.app.base.util.orFalse
import com.jar.app.base.util.textChanges
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.domain.model.KycEmailAndAadhaarProgressStatus
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.BaseConstants.KEY_OTP_SUCCESS
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.makeColoredLink
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycFragmentOtpVerificationBinding
import com.jar.app.feature_lending_kyc.impl.ui.aadhaar.captcha_bottomsheet.AadhaarCaptchaBottomSheet
import com.jar.app.feature_lending_kyc.impl.ui.aadhaar.manual_entry.AadhaarManualEntryFragmentDirections
import com.jar.app.feature_lending_kyc.impl.ui.loading.GenericLendingKycLoadingViewModel
import com.jar.app.feature_lending_kyc.impl.ui.loading.GenericLoadingArguments
import com.jar.app.feature_lending_kyc.impl.ui.pan.error_screens.PanErrorStatesArguments
import com.jar.app.feature_lending_kyc.impl.ui.pan.report_not_fetched.CreditReportNotFetchedArguments
import com.jar.app.feature_lending_kyc.impl.ui.steps.LendingKycStepsViewModel
import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.core_base.data.dto.isFromLending
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarActionPromptArgs
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarErrorScreenPrimaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarErrorScreenSecondaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportPAN
import com.jar.app.feature_lending_kyc.shared.domain.model.EmailDeliveryStatus
import com.jar.app.feature_lending_kyc.shared.domain.model.ExperianTermsAndCondition
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenPrimaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenSecondaryButtonAction
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants.IllustrationUrls.AADHAAR_NOT_LINKED_WITH_NUMBER
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants.IllustrationUrls.AADHAAR_SERVER_DOWN_URL
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.app.feature_lending_kyc.shared.util.LendingKycFlowType
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class OtpVerificationFragment :
    BaseBottomSheetDialogFragment<FeatureLendingKycFragmentOtpVerificationBinding>() {

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycFragmentOtpVerificationBinding
        get() = FeatureLendingKycFragmentOtpVerificationBinding::inflate

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    private val args by navArgs<OtpVerificationFragmentArgs>()
    private var expiresInTimerJob: Job? = null
    private var resendOtpTimerJob: Job? = null
    private var otpTimeLeft = -1L
    private var emailOtpDeliveryTimerJob: Job? = null
    private var messageId: String? = null
    private var shownEvent: String = ""
    private var clickEvent: String = ""
    private var someErrorOccurredCount = 0

    private var experianTermsAndCondition: ExperianTermsAndCondition? = null

    private val otpSheetArguments by lazy {
        serializer.decodeFromString<OtpSheetArguments>(decodeUrl(args.otpArgumentsAsString))
    }

    private val lendingLendingKycOtpVerificationFlowType by lazy { otpSheetArguments.flowType }

    private val otpSmsBroadcastReceiver by lazy { OTPSmsBroadcastReceiver() }

    private val viewModelProvider: OtpVerificationViewModelAndroid by viewModels()
    private val viewModel by lazy { viewModelProvider.getInstance() }


    private val lendingKycStepsViewModel: LendingKycStepsViewModel by activityViewModels()
    private val progressViewModel by navGraphViewModels<GenericLendingKycLoadingViewModel>(R.id.feature_lending_kyc_steps_navigation)

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(isCancellable = false, isDraggable = false)

    companion object {
        const val FROM_SCREEN_OTP = "OtpVerificationFragment"
        const val LOOKING_FOR_CREDIT_REPORT_SCREEN = "Looking For Credit Report Screen"
        const val EXPERIAN_TNC = "Experian TnC"
        const val CONSENT_TEXT_CHECKED = "Consent Text Checked"
        const val VERIFY = "Verify"
        const val RESEND_OTP = "Resend OTP"
        const val CROSS_BUTTON = "Cross button"
        const val REENTER_CAPTCHA = "Re-enter Captcha"
        const val ENTER_AADHAAR_DETAIL_SCREEN = "Enter Aadhaar Details Screen"
        const val AADHAAR_VERIFYING_DETAILS_SCREEN = "Aadhaar Verifying Details Screen"
    }

    override fun getTheme(): Int {
        return com.jar.app.core_ui.R.style.BottomSheetDialogInput
    }

    override fun setup() {
        setupUI()
        setupListeners()
        observeFlow()
    }

    private fun setupUI() {
        if (lendingLendingKycOtpVerificationFlowType == LendingKycConstants.LendingKycOtpVerificationFlowType.CREDIT_REPORT || lendingLendingKycOtpVerificationFlowType == LendingKycConstants.LendingKycOtpVerificationFlowType.AADHAAR) {
            startSMSRetrieverClient()
            EventBus.getDefault().register(this)
        }

        logAnalyticsEvent(LendingKycEventKey.OTP_BOTTOMSHEET_SHOWN)

        when (lendingLendingKycOtpVerificationFlowType) {
            LendingKycConstants.LendingKycOtpVerificationFlowType.EMAIL -> {
                shownEvent = LendingKycEventKey.Shown_EmailOTPVerificationBottomSheet
                clickEvent = LendingKycEventKey.Clicked_Buttons_EmailOTPVerificationBottomSheet
                messageId = otpSheetArguments.emailMessageId
                setEmailOtpTitleAndViews()
                startEmailOtpDeliveryTimer()
                startResendOtpTimer(otpSheetArguments.resendTime)
            }

            LendingKycConstants.LendingKycOtpVerificationFlowType.CREDIT_REPORT -> {
                shownEvent = LendingKycEventKey.Shown_EnterExperianOTPScreen
                clickEvent = LendingKycEventKey.Clicked_Button_EnterExperianOTPBottomSheet
                setCreditReportOtpTitleAndViews()
                startResendOtpTimer(otpSheetArguments.resendTime)
                viewModel.fetchExperianTermsAndCondition(otpSheetArguments.kycFeatureFlowType)
                if (otpSheetArguments.kycFeatureFlowType.isFromLending()) {
                    analyticsHandler.postEvent(LendingKycEventKey.Lending_PanCardFetchOtpSent)
                }
            }

            LendingKycConstants.LendingKycOtpVerificationFlowType.AADHAAR -> {
                shownEvent = LendingKycEventKey.Shown_AadhaarEnterOTPBottomSheet
                clickEvent = LendingKycEventKey.Clicked_Button_AadhaarEnterOTPScreen
                setAadhaarOtpTitleAndViews()
                startResendOtpTimerForAadhaar(otpSheetArguments.resendTime)
            }

            LendingKycConstants.LendingKycOtpVerificationFlowType.SELFIE -> {
                //Do Nothing
            }
        }
        analyticsHandler.postEvent(shownEvent)
    }

    private fun setupListeners() {
        binding.ivCross.setDebounceClickListener {
            analyticsHandler.postEvent(
                clickEvent,
                mapOf(
                    LendingKycEventKey.optionChosen to CROSS_BUTTON
                )
            )

            val screenName = when (lendingLendingKycOtpVerificationFlowType) {
                LendingKycConstants.LendingKycOtpVerificationFlowType.CREDIT_REPORT -> {
                    LendingKycEventKey.PAN_OTP_SCREEN
                }

                LendingKycConstants.LendingKycOtpVerificationFlowType.AADHAAR -> {
                    LendingKycEventKey.AADHAR_MANUAL_OTP_SCREEN
                }

                else -> {
                    null
                }
            }

            screenName?.let {
                analyticsHandler.postEvent(
                    LendingKycEventKey.Lending_CrossButtonClicked,
                    mapOf(
                        LendingKycEventKey.screen_name to it
                    )
                )
            }

            popBackStack(R.id.otpVerificationFragment, true)
        }

        binding.tvReenterCaptcha.setDebounceClickListener {
            analyticsHandler.postEvent(
                clickEvent,
                mapOf(
                    LendingKycEventKey.optionChosen to REENTER_CAPTCHA
                )
            )
            otpSheetArguments.aadhaarNumber?.let {
                navigateTo(
                    FeatureLendingKycStepsNavigationDirections.actionToAadhaarEnterCaptchaBottomSheet(
                        it,
                        AadhaarCaptchaBottomSheet.ENTER_AADHAAR_OTP_BOTTOM_SHEET,
                        otpSheetArguments.lenderName,
                        otpSheetArguments.kycFeatureFlowType.name
                    ),
                    popUpTo = R.id.otpVerificationFragment,
                    inclusive = true
                )
            }
        }
        binding.tvResendOtp.setDebounceClickListener {
            if (otpTimeLeft > 0) {
                when (lendingLendingKycOtpVerificationFlowType) {
                    LendingKycConstants.LendingKycOtpVerificationFlowType.EMAIL ->
                        otpSheetArguments.email?.let {
                            viewModel.requestEmailOtp(
                                it,
                                otpSheetArguments.kycFeatureFlowType
                            )
                        }

                    LendingKycConstants.LendingKycOtpVerificationFlowType.CREDIT_REPORT -> {
                        viewModel.requestCreditReportOtp(otpSheetArguments.kycFeatureFlowType)
                    }

                    LendingKycConstants.LendingKycOtpVerificationFlowType.AADHAAR -> {
                    }

                    LendingKycConstants.LendingKycOtpVerificationFlowType.SELFIE -> {
                        //Do Nothing
                    }
                }
                analyticsHandler.postEvent(
                    clickEvent,
                    mapOf(
                        LendingKycEventKey.optionChosen to RESEND_OTP,
                        LendingKycEventKey.isFromLendingFlow to (otpSheetArguments.kycFeatureFlowType.isFromLending())
                    )
                )

                logAnalyticsEvent(LendingKycEventKey.OTP_RESENT)
            }
        }

        binding.btnVerify.setDebounceClickListener {
            analyticsHandler.postEvent(
                clickEvent,
                mapOf(
                    LendingKycEventKey.optionChosen to VERIFY,
                    LendingKycEventKey.isFromLendingFlow to otpSheetArguments.kycFeatureFlowType.isFromLending()
                )
            )

            logAnalyticsEvent(LendingKycEventKey.OTP_VERIFY_CLICKED)

            if (binding.otpView.text.isNullOrEmpty().not())
                setButtonVerifyRedirection()
        }

        binding.checkboxConsent.setOnCheckedChangeListener { compoundButton, value ->
            if (value) {
                analyticsHandler.postEvent(
                    clickEvent,
                    mapOf(
                        LendingKycEventKey.optionChosen to CONSENT_TEXT_CHECKED
                    )
                )
                logAnalyticsEvent(LendingKycEventKey.CONSENT_BOX_CLICKED)
            }
            toggleMainButton(!value)
        }

        binding.otpView.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                logAnalyticsEvent(LendingKycEventKey.OTP_TYPED)
            }
        }
        binding.otpView.textChanges()
            .debounce(300)
            .onEach {
                if (binding.tvOtpError.isVisible) {
                    toggleOtpError(false)
                }
                if (it?.toString()?.isDigitsOnly().orFalse()) {
                    toggleMainButton()
                } else {
                    logAnalyticsEvent(LendingKycEventKey.INVALID_OTP_SHOWN)
                    toggleOtpError(true, getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_invalid_otp_format))
                }
            }.launchIn(uiScope)
    }

    private fun setButtonVerifyRedirection() {
        when (lendingLendingKycOtpVerificationFlowType) {
            LendingKycConstants.LendingKycOtpVerificationFlowType.EMAIL ->
                otpSheetArguments.email?.let {
                    viewModel.verifyEmailOtp(
                        it, binding.otpView.text.toString(),
                        otpSheetArguments.kycFeatureFlowType
                    )
                }

            LendingKycConstants.LendingKycOtpVerificationFlowType.CREDIT_REPORT -> {
                if (binding.checkboxConsent.isChecked) {
                    if (otpSheetArguments.shouldNotifyAfterOtpSuccess) {
                        viewModel.verifyCreditReportOtpV2(
                            otp = binding.otpView.text.toString(),
                            kycFeatureFlowType = otpSheetArguments.kycFeatureFlowType,
                            name = otpSheetArguments.nameForCreditReport,
                            panNumber = otpSheetArguments.panNumberForCreditReport
                        )
                    } else {
                        viewModel.verifyCreditReportOtp(
                            binding.otpView.text.toString(),
                            otpSheetArguments.kycFeatureFlowType
                        )
                    }
                }
            }

            LendingKycConstants.LendingKycOtpVerificationFlowType.AADHAAR -> {
                otpSheetArguments.aadhaarSessionId?.let {
                    viewModel.verifyAadhaarOtp(binding.otpView.text.toString(), it, otpSheetArguments.kycFeatureFlowType)
                }
            }

            LendingKycConstants.LendingKycOtpVerificationFlowType.SELFIE -> {
                //Do Nothing
            }
        }
    }

    private fun setEmailOtpTitleAndViews() {
        val title = getCustomStringFormatted(
            com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_sent_to_s_email_check_spam_as_well,
            otpSheetArguments.email.orEmpty()
        )
        binding.tvTitle.makeColoredLink(
            message = title,
            words = listOf(otpSheetArguments.email.orEmpty()),
            color = ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.white),
            shouldUnderlineWords = false,
            {}
        )
        binding.checkboxConsent.isVisible = false
        binding.tvIHereByConsent.isInvisible = true
        binding.tvAadhaarOtpExpiry.isVisible = false
        binding.clAadhaarResendContainer.isVisible = false
        binding.ivTopIcon.setImageResource(R.drawable.feature_lending_kyc_ic_email)
    }

    private fun setCreditReportOtpTitleAndViews() {
        val masked = StringBuilder()
        masked.append(prefs.getUserPhoneNumber()?.take(3).orEmpty())
        masked.append(" ***** ")
        masked.append(prefs.getUserPhoneNumber()?.takeLast(5).orEmpty())

        val title = getCustomStringFormatted(
            com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_sent_to_your_registered_mobile_no_s,
            masked.toString()
        )
        binding.tvTitle.makeColoredLink(
            message = title,
            words = listOf(masked.toString()),
            color = ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.white),
            shouldUnderlineWords = false,
            {}
        )

        binding.checkboxConsent.isVisible = true
        binding.tvIHereByConsent.isInvisible = false
        binding.tvIHereByConsent.isVisible = true
        binding.tvAadhaarOtpExpiry.isVisible = false
        binding.clAadhaarResendContainer.isVisible = false
        binding.ivTopIcon.setImageResource(R.drawable.feature_lending_kyc_ic_otp_message)
    }

    private fun setAadhaarOtpTitleAndViews() {
        binding.tvTitle.text =
            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_sent_to_your_registered_mobile_no)
        binding.checkboxConsent.isVisible = false
        binding.tvIHereByConsent.isVisible = false
        binding.tvIHereByConsent.isInvisible = true
        binding.clExpiryTimerContainer.isVisible = false
        binding.tvAadhaarOtpExpiry.isVisible = true
        binding.ivTopIcon.setImageResource(R.drawable.feature_lending_kyc_ic_otp_message)
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.verifyEmailOtpFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        navigateTo(
                            FeatureLendingKycStepsNavigationDirections.actionToSuccessStepDialog(
                                flowType = LendingKycFlowType.EMAIL,
                                fromScreen = "",
                                lenderName = null,
                                kycFeatureFlowType = otpSheetArguments.kycFeatureFlowType.name
                            ),
                            shouldAnimate = true,
                            popUpTo = R.id.otpVerificationFragment,
                            inclusive = true
                        )
                    },
                    onError = { message, errorCode ->
                        dismissProgressBar()
                        analyticsHandler.postEvent(
                            LendingKycEventKey.Shown_EmailOTPEdgeCases,
                            mapOf(
                                LendingKycEventKey.messageShown to message
                            )
                        )
                        when (errorCode) {
                            BaseConstants.ErrorCodesLendingKyc.Email.OTP_ENTERED_IS_INCORRECT -> {
                                toggleOtpError(true, message)
                            }

                            BaseConstants.ErrorCodesLendingKyc.Email.OTP_EXPIRED -> {
                                binding.tvResendOtp.text = getResendOtpString(0)
                            }

                            else -> {
                                message.snackBar(getRootView())
                            }
                        }
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.requestEmailOtpFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        if (it?.status == KycEmailAndAadhaarProgressStatus.VERIFIED.name) {
                            startResendOtpTimer(it?.resentOTPInSeconds.orZero())
                            getCustomStringFormatted(
                                com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_otp_sent_to_your_email_successfully_d_attempts_remaining,
                                it.attemptLeft.orZero()
                            ).snackBar(getRootView())
                        }
                    },
                    onError = { message, errorCode ->
                        dismissProgressBar()
                        if (errorCode == BaseConstants.ErrorCodesLendingKyc.Email.OTP_ATTEMPT_LIMIT_EXCEEDED) {
                            navigateTo(
                                FeatureLendingKycStepsNavigationDirections.actionToOtpLimitExceededBottomSheet(
                                    LendingKycFlowType.EMAIL
                                ),
                                popUpTo = R.id.otpVerificationFragment,
                                inclusive = true
                            )
                        } else {
                            message.snackBar(getRootView())
                        }
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.verifyCreditReportOtpV2Flow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        if (otpSheetArguments.shouldNotifyAfterOtpSuccess) {
                            notifyAfterSuccess()
                        }
                    },
                    onError = { message, errorCode ->
                        dismissProgressBar()
                        if (otpSheetArguments.shouldNotifyAfterOtpSuccess) {
                            if (errorCode == BaseConstants.ErrorCodesLendingKyc.PAN.OTP_ENTERED_IS_INCORRECT) {
                                toggleOtpError(true, message)
                            } else {
                                notifyAfterSuccess()
                            }
                        }
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.verifyCreditReportOtpFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            if (otpSheetArguments.kycFeatureFlowType.isFromLending())
                                analyticsHandler.postEvent(LendingKycEventKey.Lending_PanCardFetchOtpSubmittedSuccessfully)
                            navigateToCreditReportSuccessScreen(
                                it.creditReportPAN, it.jarVerifiedPAN.orFalse()
                            )
                        }
                    },
                    onError = { message, errorCode ->
                        dismissProgressBar()
                        when (errorCode) {
                            BaseConstants.ErrorCodesLendingKyc.PAN.OTP_ENTERED_IS_INCORRECT -> {
                                toggleOtpError(true, message)
                            }

                            BaseConstants.ErrorCodesLendingKyc.PAN.OTP_ATTEMPT_LIMIT_EXCEEDED -> {
                                navigateTo(
                                    FeatureLendingKycStepsNavigationDirections.actionToPanErrorStatesFragment(
                                        PanErrorStatesArguments(
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan_entry_attempt_limit_exceeded),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_please_try_again_tomorrow),
                                            BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                                            PanErrorScreenPrimaryButtonAction.GO_HOME,
                                            PanErrorScreenSecondaryButtonAction.CONTACT_SUPPORT,
                                            jarVerifiedPAN = false,
                                            contactMessage = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_credit_report_otp_limit_exceeded),
                                            kycFeatureFlowType = otpSheetArguments.kycFeatureFlowType
                                        )
                                    ),
                                    popUpTo = R.id.otpVerificationFragment,
                                    inclusive = true
                                )
                            }

                            BaseConstants.ErrorCodesLendingKyc.PAN.OTP_ATTEMPT_LIMIT_EXHAUSTED -> {
                                navigateTo(
                                    FeatureLendingKycStepsNavigationDirections.actionToPanErrorStatesFragment(
                                        PanErrorStatesArguments(
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_uh_oh_exclamation),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan_entry_limit_exceeded_please_come_back_tomorrow),
                                            BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                                            PanErrorScreenPrimaryButtonAction.GO_HOME,
                                            PanErrorScreenSecondaryButtonAction.CONTACT_SUPPORT,
                                            jarVerifiedPAN = false,
                                            contactMessage = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_credit_report_otp_limit_exhausted),
                                            kycFeatureFlowType = otpSheetArguments.kycFeatureFlowType
                                        )
                                    ),
                                    popUpTo = R.id.otpVerificationFragment,
                                    inclusive = true
                                )
                            }

                            BaseConstants.ErrorCodesLendingKyc.PAN.CREDIT_REPORT_DOES_NOT_EXIST -> {
                                navigateTo(
                                    FeatureLendingKycStepsNavigationDirections.actionToCreditReportNotAvailableFragment(
                                        CreditReportNotFetchedArguments(
                                            title = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan_card_not_found),
                                            description = getCustomString(
                                                com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_please_enter_your_pan_details
                                            ),
                                            assetUrl = BaseConstants.CDN_BASE_URL + LendingKycConstants.IllustrationUrls.CREDIT_REPORT_NOT_FETCHED_URL,
                                            primaryAction = PanErrorScreenPrimaryButtonAction.ENTER_PAN_MANUALLY,
                                            secondaryAction = PanErrorScreenSecondaryButtonAction.NONE,
                                            fromScreen = LOOKING_FOR_CREDIT_REPORT_SCREEN,
                                            jarVerifiedPAN = false,
                                            kycFeatureFlowType = otpSheetArguments.kycFeatureFlowType
                                        )
                                    ),
                                    popUpTo = R.id.otpVerificationFragment,
                                    inclusive = true
                                )
                            }

                            BaseConstants.ErrorCodesLendingKyc.PAN.VERIFICATION_FAILED -> {
                                navigateTo(
                                    FeatureLendingKycStepsNavigationDirections.actionToPanErrorStatesFragment(
                                        PanErrorStatesArguments(
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan_verification_failed),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_please_contact_customer_support),
                                            BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                                            PanErrorScreenPrimaryButtonAction.GO_HOME,
                                            PanErrorScreenSecondaryButtonAction.CONTACT_SUPPORT,
                                            jarVerifiedPAN = false,
                                            contactMessage = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_verification_failed),
                                            kycFeatureFlowType = otpSheetArguments.kycFeatureFlowType
                                        )
                                    ),
                                    popUpTo = R.id.otpVerificationFragment,
                                    inclusive = true
                                )
                            }

                            BaseConstants.ErrorCodesLendingKyc.PAN.CREDIT_REPORT_SEARCH_TAKING_LONGER_THAN_EXPECTED -> {
                                navigateTo(
                                    FeatureLendingKycStepsNavigationDirections.actionToPanErrorStatesFragment(
                                        PanErrorStatesArguments(
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_hmm_this_is_taking_some_time),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_credit_report_search_taking_longer_than_usual),
                                            BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                                            if (false.orFalse()) PanErrorScreenPrimaryButtonAction.USE_PAN_SAVED_WITH_JAR else PanErrorScreenPrimaryButtonAction.ENTER_PAN_MANUALLY,
                                            if (false.orFalse()) PanErrorScreenSecondaryButtonAction.ENTER_PAN_MANUALLY else PanErrorScreenSecondaryButtonAction.NONE,
                                            jarVerifiedPAN = false,
                                            kycFeatureFlowType = otpSheetArguments.kycFeatureFlowType
                                        )
                                    ),
                                    popUpTo = R.id.otpVerificationFragment,
                                    inclusive = true
                                )
                            }

                            BaseConstants.ErrorCode.SOME_ERROR_OCCURRED_PLEASE_TRY_AGAIN -> {
                                someErrorOccurredCount.inc()
                                if (someErrorOccurredCount >= 3)
                                    navigateTo(
                                        FeatureLendingKycStepsNavigationDirections.actionToPanErrorStatesFragment(
                                            PanErrorStatesArguments(
                                                getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_uh_oh_exclamation),
                                                getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_your_pan_details_could_not_be_verified_please_contact_support),
                                                BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                                                PanErrorScreenPrimaryButtonAction.GO_HOME,
                                                PanErrorScreenSecondaryButtonAction.CONTACT_SUPPORT,
                                                jarVerifiedPAN = false,
                                                contactMessage = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_verification_failed),
                                                kycFeatureFlowType = otpSheetArguments.kycFeatureFlowType
                                            )
                                        ),
                                        popUpTo = R.id.otpVerificationFragment,
                                        inclusive = true
                                    )
                            }

                            else -> {
                                message.snackBar(getRootView())
                            }
                        }
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.requestCreditReportOtpFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        startResendOtpTimer(it?.resentOTPInSeconds.orZero())
                        getCustomStringFormatted(
                            com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_otp_sent_to_your_number_successfully_d_attempts_remaining,
                            it?.attemptLeft.orZero()
                        ).snackBar(getRootView())
                    },
                    onError = { message, errorCode ->
                        dismissProgressBar()
                        when (errorCode) {
                            BaseConstants.ErrorCodesLendingKyc.PAN.OTP_ATTEMPT_LIMIT_EXCEEDED -> {
                                navigateTo(
                                    FeatureLendingKycStepsNavigationDirections.actionToCreditReportOtpResendLimitExceededBottomSheet(
                                        title = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_come_back_later),
                                        desc = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_please_try_again_tomorrow),
                                        jarVerifiedPAN =false,
                                        isComeBackLaterFlow = true,
                                        kycFeatureFlowType = otpSheetArguments.kycFeatureFlowType.name
                                    ), true,
                                    popUpTo = R.id.otpVerificationFragment,
                                    inclusive = true
                                )
                            }

                            BaseConstants.ErrorCodesLendingKyc.PAN.OTP_ATTEMPT_LIMIT_EXHAUSTED -> {
                                navigateTo(
                                    FeatureLendingKycStepsNavigationDirections.actionToCreditReportOtpResendLimitExceededBottomSheet(
                                        title = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_otp_resned_limit_exceeded),
                                        desc = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_please_enter_your_pan_details),
                                        jarVerifiedPAN = false,
                                        isComeBackLaterFlow = false,
                                        kycFeatureFlowType = otpSheetArguments.kycFeatureFlowType.name
                                    ), true,
                                    popUpTo = R.id.otpVerificationFragment,
                                    inclusive = true
                                )
                            }
                        }
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.fetchExperianTnCFlow.collect(
                    onSuccess = {
                        experianTermsAndCondition = it?.experianTermsAndCondition
                        viewModel.fetchExperianConsent(otpSheetArguments.kycFeatureFlowType)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.fetchExperianConsentFlow.collect(
                    onSuccess = {
                        it?.let {
                            setHyperLinkInConsentText(
                                it.experianTermsAndCondition.description,
                                it.experianTermsAndCondition.hyperLink
                            )
                        }
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.verifyAadhaarOtpFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        if (otpSheetArguments.kycFeatureFlowType.isFromLending()) {
                            analyticsHandler.postEvent(LendingKycEventKey.Lending_AadharFetchOtpSubmittedSuccessfully)
                        }
                        simulateAadhaarOtpVerificationSuccess()
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                        simulateAadhaarOtpVerificationSuccess()
                    },
                    onError = { message, errorCode ->
                        dismissProgressBar()
                        when (errorCode) {
                            BaseConstants.ErrorCodesLendingKyc.Aadhaar.NO_MOBILE_LINKED -> {
                                analyticsHandler.postEvent(
                                    if (otpSheetArguments.kycFeatureFlowType.isFromLending()) LendingKycEventKey.Lending_NoMobileLinkedToAadharScreenShown
                                    else LendingKycEventKey.Shown_NoMobileLinkedToAadhaarScreen,
                                    mapOf(LendingKycEventKey.fromScreen to ENTER_AADHAAR_DETAIL_SCREEN)
                                )
                                navigateTo(
                                    AadhaarManualEntryFragmentDirections.actionToAadhaarActionPromptFragment(
                                        AadhaarActionPromptArgs(
                                            BaseConstants.CDN_BASE_URL + AADHAAR_NOT_LINKED_WITH_NUMBER,
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_no_mobile_linked_to_aadhaar),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_looks_like_no_mobile_linked_to_aadhaar_message),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_re_enter_aadhaar_number),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_contact_support),
                                            AadhaarErrorScreenPrimaryButtonAction.GO_BACK,
                                            AadhaarErrorScreenSecondaryButtonAction.CONTACT_SUPPORT,
                                            true,
                                            contactMessage = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_my_aadhaar_is_not_linked_help_me)
                                        )
                                    ),
                                    popUpTo = R.id.otpVerificationFragment,
                                    inclusive = true
                                )
                            }

                            BaseConstants.ErrorCodesLendingKyc.Aadhaar.UNABLE_TO_REACH_UIDAI -> {
                                analyticsHandler.postEvent(
                                    if (otpSheetArguments.kycFeatureFlowType.isFromLending()) LendingKycEventKey.Lending_AadharServerDownScreenShown
                                    else LendingKycEventKey.Shown_AadhaarServerDownScreen
                                )
                                navigateTo(
                                    AadhaarManualEntryFragmentDirections.actionToAadhaarActionPromptFragment(
                                        AadhaarActionPromptArgs(
                                            BaseConstants.CDN_BASE_URL + AADHAAR_SERVER_DOWN_URL,
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_aadhaar_server_down),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_looks_like_aadhaar_server_down_message),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_go_home),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_),
                                            AadhaarErrorScreenPrimaryButtonAction.GO_HOME,
                                            AadhaarErrorScreenSecondaryButtonAction.NONE,
                                            true,
                                            kycFeatureFlowType = otpSheetArguments.kycFeatureFlowType
                                        )
                                    ),
                                    popUpTo = R.id.otpVerificationFragment,
                                    inclusive = true
                                )
                            }

                            BaseConstants.ErrorCodesLendingKyc.Aadhaar.INVALID_OTP -> {
                                toggleOtpError(true, message)
                                logAnalyticsEvent(LendingKycEventKey.INVALID_OTP_SHOWN)
                                analyticsHandler.postEvent(
                                    LendingKycEventKey.Shown_AadhaarOTPEdgeCases,
                                    mapOf(LendingKycEventKey.textDisplayed to message)
                                )
                            }

                            BaseConstants.ErrorCodesLendingKyc.Aadhaar.INVALID_OTP_FORMAT -> {
                                toggleOtpError(true, message)
                                analyticsHandler.postEvent(
                                    LendingKycEventKey.Shown_AadhaarOTPEdgeCases,
                                    mapOf(LendingKycEventKey.textDisplayed to message)
                                )
                            }

                            BaseConstants.ErrorCodesLendingKyc.Aadhaar.OTP_LIMIT_EXHAUSTED -> {
                                navigateTo(
                                    FeatureLendingKycStepsNavigationDirections.actionToOtpLimitExceededBottomSheet(
                                        LendingKycFlowType.EMAIL
                                    ),
                                    popUpTo = R.id.otpVerificationFragment,
                                    inclusive = true
                                )
                            }

                            BaseConstants.ErrorCodesLendingKyc.Aadhaar.AADHAAR_PAN_MISMATCH -> {
                                lendingKycStepsViewModel.fetchKycProgress(
                                    WeakReference(requireActivity()), false, shouldNavigate = false
                                )
                                val description =
                                    getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_your_pan_aadhaar_details_dont_match)
                                analyticsHandler.postEvent(
                                    LendingKycEventKey.Shown_AadhaarPANMismatchScreen,
                                    mapOf(
                                        LendingKycEventKey.textDisplayed to description,
                                        LendingKycEventKey.fromScreen to otpSheetArguments.fromScreen,
                                    )
                                )
                                navigateTo(
                                    AadhaarManualEntryFragmentDirections.actionToAadhaarActionPromptFragment(
                                        AadhaarActionPromptArgs(
                                            BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_uh_oh_exclamation),
                                            description,
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_re_enter_aadhaar_details),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_contact_support),
                                            AadhaarErrorScreenPrimaryButtonAction.EDIT_AADHAAR,
                                            AadhaarErrorScreenSecondaryButtonAction.CONTACT_SUPPORT,
                                            contactMessage = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_my_pan_and_aadhaar_details_are_not_matching),
                                            kycFeatureFlowType = otpSheetArguments.kycFeatureFlowType
                                        )
                                    ),
                                    popUpTo = R.id.otpVerificationFragment,
                                    inclusive = true
                                )
                            }

                            BaseConstants.ErrorCodesLendingKyc.Aadhaar.AADHAAR_PAN_MISMATCH_SUPPORT -> {
                                lendingKycStepsViewModel.fetchKycProgress(
                                    WeakReference(requireActivity()), false, shouldNavigate = false
                                )
                                val description =
                                    getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_your_pan_aadhaar_details_dont_match_try_contacting_support)
                                analyticsHandler.postEvent(
                                    LendingKycEventKey.Shown_AadhaarPANMismatchScreen,
                                    mapOf(
                                        LendingKycEventKey.textDisplayed to description,
                                        LendingKycEventKey.fromScreen to otpSheetArguments.fromScreen,
                                    )
                                )
                                navigateTo(
                                    AadhaarManualEntryFragmentDirections.actionToAadhaarActionPromptFragment(
                                        AadhaarActionPromptArgs(
                                            BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_uh_oh_exclamation),
                                            description,
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_back_to_home),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_contact_support),
                                            AadhaarErrorScreenPrimaryButtonAction.GO_HOME,
                                            AadhaarErrorScreenSecondaryButtonAction.CONTACT_SUPPORT,
                                            contactMessage = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_my_pan_and_aadhaar_details_are_not_matching),
                                            kycFeatureFlowType = otpSheetArguments.kycFeatureFlowType
                                        )
                                    ),
                                    popUpTo = R.id.otpVerificationFragment,
                                    inclusive = true
                                )
                            }

                            else -> {
                                message.snackBar(getRootView())
                            }
                        }
                    }
                )
            }
        }
        progressViewModel.genericLoadingOnAutoDismissViewModel.observe(viewLifecycleOwner) {
            if (it.isDismissingAfterSuccess && it.fromScreen == FROM_SCREEN_OTP) {
                navigateTo(
                    OtpVerificationFragmentDirections.actionToSuccessStepDialog(
                        flowType = LendingKycFlowType.AADHAAR,
                        fromScreen = otpSheetArguments.fromScreen,
                        lenderName = otpSheetArguments.lenderName,
                        kycFeatureFlowType = otpSheetArguments.kycFeatureFlowType.name
                    ),
                    popUpTo = R.id.otpVerificationFragment,
                    inclusive = true
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.emailDeliveryStatusFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        when (it?.deliveryStatus) {
                            EmailDeliveryStatus.DELIVERED.name -> {}
                            EmailDeliveryStatus.BOUNCE.name -> {
                                navigateTo(
                                    OtpVerificationFragmentDirections.actionOtpVerificationFragmentToEmailDeliveryStatusDialog(
                                        otpSheetArguments.email.orEmpty(),
                                        otpSheetArguments.emailMessageId.orEmpty()
                                    ), popUpTo = R.id.otpVerificationFragment, inclusive = true
                                )
                            }

                            EmailDeliveryStatus.COMPLAINT.name -> {
                                navigateTo(
                                    OtpVerificationFragmentDirections.actionOtpVerificationFragmentToEmailDeliveryStatusDialog(
                                        otpSheetArguments.email.orEmpty(),
                                        otpSheetArguments.emailMessageId.orEmpty()
                                    ), popUpTo = R.id.otpVerificationFragment, inclusive = true
                                )
                            }
                        }
                    },
                    onError = { message, errorCode ->
                        dismissProgressBar()
                        if (errorCode == BaseConstants.ErrorCodesLendingKyc.Email.OTP_ATTEMPT_LIMIT_EXCEEDED)
                            navigateTo(
                                FeatureLendingKycStepsNavigationDirections.actionToOtpLimitExceededBottomSheet(
                                    LendingKycFlowType.EMAIL
                                )
                            )
                    }
                )
            }
        }
    }

    private fun simulateAadhaarOtpVerificationSuccess() {
        logAnalyticsEvent(LendingKycEventKey.OTP_VERIFICATION_SUCCESSFUL)
        uiScope.launch {
            navigateTo(
                AadhaarManualEntryFragmentDirections.actionToGenericLoadingDialog(
                    GenericLoadingArguments(
                        getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_otp_verification_successful),
                        getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_empty),
                        null,
                        R.drawable.feature_lending_kyc_ic_otp_message
                    )
                )
            )
            progressViewModel.showProgressSuccess(
                getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_otp_verification_successful),
                BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.SMALL_CHECK
            )
            delay(1000L)
            analyticsHandler.postEvent(
                LendingKycEventKey.Shown_AadhaarVerifyingDetailsScreen
            )
            progressViewModel.updateGenericLoadingTitle(getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_verifying_details))
            progressViewModel.updateAssetUrl(
                BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.VERIFYING,
                false
            )
            progressViewModel.updateGenericLoadingDescription(getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_please_wait_while_we_verify_your_details_from_uidai))
            progressViewModel.dismissGenericLoadingAfterMillis(1000L, true, FROM_SCREEN_OTP)
        }
    }

    private fun toggleMainButton(disableAnyway: Boolean = false) {
        val shouldEnable = if (disableAnyway) false
        else (binding.otpView.text?.length == 6)
        if (otpSheetArguments.flowType == LendingKycConstants.LendingKycOtpVerificationFlowType.CREDIT_REPORT) {
            binding.btnVerify.setDisabled(!(binding.checkboxConsent.isChecked && shouldEnable))
        } else if (otpSheetArguments.flowType != LendingKycConstants.LendingKycOtpVerificationFlowType.CREDIT_REPORT) {
            binding.btnVerify.setDisabled(!shouldEnable)
        }
    }

    private fun toggleOtpError(isError: Boolean, errorMsg: String = "") {
        binding.otpView.setLineColor(
            ContextCompat.getColor(
                requireContext(),
                if (isError) com.jar.app.core_ui.R.color.color_EB6A6E else com.jar.app.core_ui.R.color.white
            )
        )
        if (isError) {
            binding.tvOtpError.text = errorMsg
        }
        binding.tvOtpError.isVisible = isError
    }

    private fun navigateToCreditReportSuccessScreen(
        creditReportPAN: CreditReportPAN?, jarVerifiedPAN: Boolean
    ) {
        navigateTo(
            FeatureLendingKycStepsNavigationDirections.actionToCreditReportFetchSuccessDialog(
                creditReportPAN,
                jarVerifiedPAN,
                otpSheetArguments.kycFeatureFlowType.name
            ),
            shouldAnimate = true
        )
    }

    private fun notifyAfterSuccess() {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(KEY_OTP_SUCCESS, true)
        popBackStack()
    }

    private fun startEmailOtpDeliveryTimer() {
        emailOtpDeliveryTimerJob?.cancel()
        emailOtpDeliveryTimerJob = uiScope.countDownTimer(
            45 * 1000L,
            intervalInMillis = 1000L,
            onInterval = {
            },
            onFinished = {
                if (messageId.isNullOrEmpty().not() && otpSheetArguments.email.isNullOrEmpty()
                        .not()
                ) {
                    dismissProgressBar()
                    viewModel.getEmailDeliveryStatus(otpSheetArguments.email!!, messageId!!,otpSheetArguments.kycFeatureFlowType)
                }
            }
        )
    }

    private fun startResendOtpTimer(timeInSecs: Long) {
        resendOtpTimerJob?.cancel()
        resendOtpTimerJob = uiScope.countDownTimer(
            timeInSecs * 1000L,
            onInterval = {
                otpTimeLeft = it
                binding.tvResendOtp.text = getResendOtpString(it)
            },
            onFinished = {
                otpTimeLeft = -1L
                binding.tvResendOtp.text = getResendOtpString(0)
            }
        )
    }

    private fun startResendOtpTimerForAadhaar(timeInSecs: Long) {
        resendOtpTimerJob?.cancel()
        resendOtpTimerJob = uiScope.countDownTimer(
            timeInSecs * 1000L,
            onInterval = {
                otpTimeLeft = it
                binding.tvAadhaarOtpExpiry.text = getAadhaarExpiryString(it)
            },
            onFinished = {
                otpTimeLeft = -1L
                getAadhaarExpiryString(0)
                binding.tvAadhaarOtpExpiry.isVisible = false
                binding.clAadhaarResendContainer.isVisible = true
            }
        )
    }

    private fun getResendOtpString(millisLeft: Long): Spannable {
        val spannableBuilder = buildSpannedString {
            if (millisLeft > 0) {
                val t1 =
                    getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_resend_otp).plus(" ")
                val t2 = millisLeft.milliSecondsToCountDown().toSpannable()
                append(t1)
                t2.setSpan(
                    ForegroundColorSpan(getColor(com.jar.app.core_ui.R.color.white)),
                    0,
                    t2.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                append(t2)
                binding.tvResendOtp.isEnabled = false
                binding.tvResendOtp.isClickable = false
                binding.tvResendOtp.paintFlags =
                    binding.tvResendOtp.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            } else {
                binding.tvResendOtp.isEnabled = true
                binding.tvResendOtp.isClickable = true
                binding.tvResendOtp.paintFlags =
                    binding.tvResendOtp.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                append(getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_resend_otp))
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

    private fun getAadhaarExpiryString(millisLeft: Long): Spannable {
        binding.clExpiryTimerContainer.isVisible = false
        val spannableBuilder = buildSpannedString {
            if (millisLeft > 0) {
                binding.tvAadhaarOtpExpiry.isVisible = true
                binding.clAadhaarResendContainer.isVisible = false
                val t1 = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_it_usually_takes).plus(" ")
                val t2 = millisLeft.milliSecondsToCountDown().toSpannable()
                append(t1)


                t2.setSpan(
                    ForegroundColorSpan(getColor(com.jar.app.core_ui.R.color.white)),
                    0,
                    t2.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                append(t2)

                append(" ")
                append(getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_time_to_get_aadhaar_otp))
            } else {
                binding.tvAadhaarOtpExpiry.isVisible = false
                binding.clAadhaarResendContainer.isVisible = true
            }
        }

        return spannableBuilder.toSpannable()
    }

    private fun getColor(colorId: Int) = ContextCompat.getColor(requireContext(), colorId)

    private fun setHyperLinkInConsentText(message: String?, hyperLink: String?) {
        binding.tvIHereByConsent.makeColoredLink(
            message = message ?: getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_i_hereby_consent_jar),
            words = listOf(
                hyperLink ?: getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_i_also_agree_with_t_and_c)
            ),
            color = ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.white),
            shouldUnderlineWords = true,
            {
                experianTermsAndCondition?.let {
                    analyticsHandler.postEvent(
                        clickEvent, mapOf(
                            LendingKycEventKey.optionChosen to EXPERIAN_TNC
                        )
                    )
                    navigateTo(
                        OtpVerificationFragmentDirections.actionOtpVerificationFragmentToKycTermsAndConditionBottomSheet(
                            it
                        ),
                        shouldAnimate = true
                    )
                }
            }
        )
    }

    private fun startSMSRetrieverClient() {
        val task1 =
            SmsRetriever.getClient(requireContext()).startSmsUserConsent(prefs.getUserPhoneNumber())
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

    private fun logAnalyticsEvent(eventName: String) {
        if (otpSheetArguments.kycFeatureFlowType.isFromLending()) {
            val eventKey = when (lendingLendingKycOtpVerificationFlowType) {
                LendingKycConstants.LendingKycOtpVerificationFlowType.AADHAAR -> {
                    LendingKycEventKey.Lending_AadharOtpFlow
                }

                LendingKycConstants.LendingKycOtpVerificationFlowType.CREDIT_REPORT -> {
                    LendingKycEventKey.Lending_PANOtpFlow
                }

                else -> {
                    null
                }
            }
            eventKey?.let {
                analyticsHandler.postEvent(
                    event = it,
                    values = mapOf(
                        LendingKycEventKey.action to eventName,
                        LendingKycEventKey.fromScreen to otpSheetArguments.fromScreen
                    )
                )
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onOTPReceivedEvent(otpReceivedEvent: OTPReceivedEvent) {
        EventBus.getDefault().removeStickyEvent(otpReceivedEvent)
        val otp = otpReceivedEvent.originalMessage.getOtp(6).orEmpty()
        binding.otpView.setText(otp)
        analyticsHandler.postEvent(
            EventKey.OTP_SMSDetected,
            mapOf(
                LendingKycEventKey.isFromLendingFlow to otpSheetArguments.kycFeatureFlowType.isFromLending()
            )
        )

        logAnalyticsEvent(LendingKycEventKey.OTP_FETCHED)
        toggleMainButton()
        when (lendingLendingKycOtpVerificationFlowType) {
            LendingKycConstants.LendingKycOtpVerificationFlowType.CREDIT_REPORT -> {
                if (binding.checkboxConsent.isChecked)
                    viewModel.verifyCreditReportOtp(otp, otpSheetArguments.kycFeatureFlowType)
            }

            LendingKycConstants.LendingKycOtpVerificationFlowType.AADHAAR -> {
                viewModel.verifyAadhaarOtp(otp, otpSheetArguments.aadhaarSessionId.orEmpty(),otpSheetArguments.kycFeatureFlowType)
            }

            else -> {}
        }
    }

    //This is to Auto-fill Aadhaar messages, which cannot be Auto-filled by SmsSmsRetriever
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onNonJarMessageReceived(onNewMessageEvent: OnNewMessageEvent) {
        EventBus.getDefault().removeStickyEvent(onNewMessageEvent)
        val message = onNewMessageEvent.message
        val otp = onNewMessageEvent.message.getOtp(6) ?: ""
        if (message.contains(
                LendingKycConstants.AADHAAR_STRING,
                ignoreCase = true
            ) && otp.isNotEmpty()
        ) {
            binding.otpView.setText(otp)
            toggleMainButton()
            if (lendingLendingKycOtpVerificationFlowType == LendingKycConstants.LendingKycOtpVerificationFlowType.AADHAAR) {
                viewModel.verifyAadhaarOtp(otp, otpSheetArguments.aadhaarSessionId.orEmpty(), otpSheetArguments.kycFeatureFlowType)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (lendingLendingKycOtpVerificationFlowType == LendingKycConstants.LendingKycOtpVerificationFlowType.CREDIT_REPORT || lendingLendingKycOtpVerificationFlowType == LendingKycConstants.LendingKycOtpVerificationFlowType.AADHAAR) {
            activity?.unregisterReceiver(otpSmsBroadcastReceiver)
            EventBus.getDefault().unregister(this)
        }
    }
}
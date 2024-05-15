package com.jar.app.feature_lending_kyc.impl.ui.aadhaar.captcha_bottomsheet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.base.util.dp
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.util.orZero
import com.jar.app.base.util.textChanges
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycBottomSheetAadhaarCaptchaBinding
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarErrorScreenPrimaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarErrorScreenSecondaryButtonAction
import com.jar.app.feature_lending_kyc.impl.ui.aadhaar.manual_entry.AadhaarManualEntryFragmentDirections
import com.jar.app.feature_lending_kyc.impl.ui.aadhaar.manual_entry.AadhaarManualEntryViewModelAndroid
import com.jar.app.feature_lending_kyc.impl.ui.loading.GenericLendingKycLoadingViewModel
import com.jar.app.feature_lending_kyc.impl.ui.loading.GenericLoadingArguments
import com.jar.app.feature_lending_kyc.impl.ui.otp.OtpSheetArguments
import com.jar.app.core_base.data.dto.getKycFeatureFlowType
import com.jar.app.core_base.data.dto.isFromP2POrLending
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarActionPromptArgs
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarCaptcha
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants.LottieUrls.SMALL_CHECK
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class AadhaarCaptchaBottomSheet :
    BaseBottomSheetDialogFragment<FeatureLendingKycBottomSheetAadhaarCaptchaBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    private val viewModelProvider by viewModels<AadhaarManualEntryViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }
    private val args by navArgs<AadhaarCaptchaBottomSheetArgs>()
    private val progressViewModel by navGraphViewModels<GenericLendingKycLoadingViewModel>(R.id.feature_lending_kyc_steps_navigation)
    private var aadhaarCaptcha: AadhaarCaptcha? = null
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycBottomSheetAadhaarCaptchaBinding
        get() = FeatureLendingKycBottomSheetAadhaarCaptchaBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(isCancellable = false, isDraggable = false)

    companion object {
        private const val MINIMUM_CAPTCHA_CHARACTER_ENTER = 3
        const val FROM_SCREEN_CAPTCHA = "AadhaarCaptchaBottomSheet"
        const val AADHAAR_OCR_CAPTCHA_BOTTOM_SHEET = "Aadhaar OCR Captcha BottomSheet"
        const val AADHAAR_OCR_EXTRACTED_DETAILS = "Aadhaar OCR Extracted Details"
        const val ENTER_AADHAAR_OTP_BOTTOM_SHEET = "Enter Aadhaar OTP BottomSheet"
        const val AADHAAR_OTP_ATTEMPT_LIMIT_EXCEEDED_BOTTOM_SHEET =
            "Aadhaar OTP Attempt Limit Exceeded BottomSheet"
        const val AADHAAR_OTP_EXPIRED_BOTTOM_SHEET = "Aadhaar OTP Expired BottomSheet"
        const val AADHAAR_OCR_ENTER_CAPTCHA_BOTTOM_SHEET = "Aadhaar OCR Enter Captcha BottomSheet"
        const val REFRESH_CAPTCHA_ICON = "Refresh Captcha Icon"
        const val VERIFY = "Verify"
        const val CROSS_BUTTON = "Cross Button"
        const val AADHAAR_OCR_FLOW = "Aadhaar OCR Flow"
    }

    override fun getTheme(): Int {
        return com.jar.app.core_ui.R.style.BottomSheetDialogInput
    }

    override fun setup() {
        setupUI()
        observeLiveData()
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.captchaResponseFlow.collectUnwrapped(
                    onLoading = {
                        shouldShowCaptchaError(false)
                        shouldShowCaptchaProgress(true)
                    },
                    onSuccess = {
                        it.data?.let {
                            aadhaarCaptcha = it
                            shouldShowCaptchaError(false)
                            shouldShowCaptchaProgress(false)
                            viewModelProvider.getCaptchaImage(it)
                        }
                    },
                    onError = { message,errorCode->
                        shouldShowCaptchaProgress(false)
                    }
                )
            }
        }
        viewModelProvider.captchaBitmapLiveData.observe(viewLifecycleOwner) {
            Glide.with(requireContext())
                .load(it)
                .transform(RoundedCorners(4.dp))
                .into(binding.ivCaptcha)
            binding.etCaptcha.setText("")
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.aadhaarOtpResponseFlow.collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        if (it.success) {
                            //Show OTP Screen
                            simulateOtpSentProgress()
                        } else {
                            //show error screen based on error code
                            when (it.errorCode?.toString()) {
                                BaseConstants.ErrorCodesLendingKyc.Aadhaar.INVALID_CAPTCHA -> { //Invalid captcha
                                    shouldShowCaptchaError(true, it.errorMessage)
                                    refreshCaptcha()
                                    val message =
                                        getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_captcha_refreshed_enter_again)
                                    message.snackBar(
                                        binding.root
                                    )
                                    analyticsHandler.postEvent(
                                        LendingKycEventKey.Shown_AadhaarOCRCaptchaEdgeCase,
                                        mapOf(LendingKycEventKey.errorMsgShown to message)
                                    )
                                }
                                BaseConstants.ErrorCodesLendingKyc.Aadhaar.NO_MOBILE_LINKED -> { //Aadhaar number doesn't have mobile number
                                    analyticsHandler.postEvent(
                                        LendingKycEventKey.Shown_NoMobileLinkedToAadhaarScreen,
                                        mapOf(LendingKycEventKey.fromScreen to AADHAAR_OCR_CAPTCHA_BOTTOM_SHEET)
                                    )
                                    navigateTo(
                                        AadhaarManualEntryFragmentDirections.actionToAadhaarActionPromptFragment(
                                            AadhaarActionPromptArgs(
                                                BaseConstants.CDN_BASE_URL + LendingKycConstants.IllustrationUrls.AADHAAR_NOT_LINKED_WITH_NUMBER,
                                                getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_no_mobile_linked_to_aadhaar),
                                                getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_looks_like_no_mobile_linked_to_aadhaar_message),
                                                getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_re_enter_aadhaar_number),
                                                getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_contact_support),
                                                AadhaarErrorScreenPrimaryButtonAction.GO_BACK,
                                                AadhaarErrorScreenSecondaryButtonAction.CONTACT_SUPPORT,
                                                true,
                                                contactMessage = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_my_aadhaar_is_not_linked_help_me)
                                            )
                                        )
                                    )
                                }
                                BaseConstants.ErrorCodesLendingKyc.Aadhaar.UNABLE_TO_REACH_UIDAI -> { //Unable to reach Aadhaar UIDAI at the moment
                                    analyticsHandler.postEvent(
                                        LendingKycEventKey.Shown_AadhaarServerDownScreen
                                    )
                                    navigateTo(
                                        AadhaarManualEntryFragmentDirections.actionToAadhaarActionPromptFragment(
                                            AadhaarActionPromptArgs(
                                                BaseConstants.CDN_BASE_URL + LendingKycConstants.IllustrationUrls.AADHAAR_SERVER_DOWN_URL,
                                                getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_aadhaar_server_down),
                                                getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_looks_like_aadhaar_server_down_message),
                                                getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_go_home),
                                                getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_),
                                                AadhaarErrorScreenPrimaryButtonAction.GO_HOME,
                                                AadhaarErrorScreenSecondaryButtonAction.NONE,
                                                true
                                            )
                                        )
                                    )
                                }
                                else -> {
                                    it.errorMessage?.let {
                                        it.snackBar(binding.root)
                                        refreshCaptcha()
                                    }
                                }
                            }

                        }
                    },
                    onError = { _,_ ->
                        dismissProgressBar()
                    }
                )
            }
        }
        progressViewModel.genericLoadingOnAutoDismissViewModel.observe(viewLifecycleOwner) {
            if (it.isDismissingAfterSuccess && it.fromScreen == FROM_SCREEN_CAPTCHA) {
                val args = encodeUrl(
                    serializer.encodeToString(
                        OtpSheetArguments(
                            flowType = LendingKycConstants.LendingKycOtpVerificationFlowType.AADHAAR,
                            expiresInTime = 30L,
                            resendTime = 30L,
                            email = null,
                            aadhaarSessionId = aadhaarCaptcha?.sessionId,
                            aadhaarNumber = args.aadhaarNumber,
                            emailMessageId = null,
                            fromScreen = args.fromScreen,
                            lenderName= args.lenderName,
                            kycFeatureFlowType = getKycFeatureFlowType(args.kycFeatureFlowType)
                        )
                    )
                )
                navigateTo(
                    AadhaarManualEntryFragmentDirections.actionToOtpVerificationFragment(args),
                    popUpTo = R.id.aadhaarCaptchaBottomSheet,
                    inclusive = true
                )
            }
        }
    }

    private fun setupUI() {
        refreshCaptcha()
        binding.btnVerify.setDisabled(true)
        setupClickListener()
        analyticsHandler.postEvent(
            LendingKycEventKey.Shown_AadhaarEnterCaptchaBottomSheet,
            mapOf(LendingKycEventKey.fromScreen to args.fromScreen)
        )
        binding.clConsentHolder.isVisible = getKycFeatureFlowType(args.kycFeatureFlowType).isFromP2POrLending()
        if (getKycFeatureFlowType(args.kycFeatureFlowType).isFromP2POrLending()){
            binding.tvIHereByConsent.text = getCustomStringFormatted(
                com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_i_hereby_give_consent_for_my_aadhaar_s,
                args.lenderName.orEmpty()
            )
        }
    }

    private fun shouldDisableActionButton(shouldDisable:Boolean){
        val disable = if (getKycFeatureFlowType(args.kycFeatureFlowType).isFromP2POrLending())
            shouldDisable || binding.checkboxConsent.isChecked.not()
        else  shouldDisable
        binding.btnVerify.setDisabled(disable)
    }
    private fun sendEventForClickButton(optionChosen: String) {
        analyticsHandler.postEvent(
            LendingKycEventKey.Clicked_AadhaarEnterCaptchaBottomSheet,
            mapOf(LendingKycEventKey.optionChosen to optionChosen)
        )
    }

    private fun setupClickListener() {
        binding.ivRefreshCaptcha.setDebounceClickListener {
            sendEventForClickButton(REFRESH_CAPTCHA_ICON)
            viewModel.fetchAadhaarCaptcha(getKycFeatureFlowType(args.kycFeatureFlowType))
            binding.etCaptcha.setText("")
        }
        binding.checkboxConsent.setOnCheckedChangeListener { _, isChecked ->
            val shouldDisable = isChecked.not() ||
                    binding.etCaptcha.text?.length.orZero() < MINIMUM_CAPTCHA_CHARACTER_ENTER

            if (isChecked){
                analyticsHandler.postEvent(
                    LendingKycEventKey.Lending_Checkbox_Clicked,
                    mapOf(
                        LendingKycEventKey.screen_name to LendingKycEventKey.AADHAR_CAPTCHA_BOTTOM_SHEET,
                        LendingKycEventKey.check_box to LendingKycEventKey.Aadhar_Consent
                    )
                )
            }
            shouldDisableActionButton(shouldDisable)
        }
        binding.etCaptcha.textChanges()
            .debounce(100)
            .onEach {
                it?.let {
                    shouldDisableActionButton(it.length < MINIMUM_CAPTCHA_CHARACTER_ENTER)
                    binding.ivClear.isVisible = it.isNotEmpty()
                }
            }
            .launchIn(uiScope)
        binding.ivClose.setDebounceClickListener {
            sendEventForClickButton(CROSS_BUTTON)
            popBackStack()
        }
        binding.btnVerify.setDebounceClickListener {
            sendEventForClickButton(VERIFY)
            viewModel.requestAadhaarOtp(
                args.aadhaarNumber,
                binding.etCaptcha.text.toString(),
                viewModelProvider.aadhaarCaptcha?.sessionId ?: "",
                getKycFeatureFlowType(args.kycFeatureFlowType)
            )
        }
        binding.ivClear.setDebounceClickListener {
            binding.etCaptcha.setText("")
        }
    }

    private fun refreshCaptcha() {
        viewModel.fetchAadhaarCaptcha(getKycFeatureFlowType(args.kycFeatureFlowType))
    }

    private fun shouldShowCaptchaError(shouldShow: Boolean, errorMessage: String? = null) {
        binding.tvError.isVisible = shouldShow
        binding.tvError.text = errorMessage ?: ""
        binding.etCaptcha.background =
            ContextCompat.getDrawable(
                requireContext(),
                if (shouldShow) com.jar.app.core_ui.R.drawable.core_ui_bg_rounded_2e2942_outline_eb6a6e_10dp
                else com.jar.app.core_ui.R.drawable.core_ui_bg_rounded_2e2942_10dp
            )
    }

    private fun shouldShowCaptchaProgress(shouldShow: Boolean) {
        binding.pbCaptcha.isVisible = shouldShow
        binding.ivRefreshCaptcha.isInvisible = shouldShow
    }

    private fun simulateOtpSentProgress() {
        uiScope.launch {
            analyticsHandler.postEvent(
                LendingKycEventKey.Shown_AadhaarSendingOTPScreen,
                mapOf(LendingKycEventKey.fromScreen to AADHAAR_OCR_ENTER_CAPTCHA_BOTTOM_SHEET)
            )
            navigateTo(
                AadhaarManualEntryFragmentDirections.actionToGenericLoadingDialog(
                    GenericLoadingArguments(
                        getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_sending_otp_on_your_mobile),
                        getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_uidai_will_send_otp_on_your_mobile_message),
                        null,
                        R.drawable.feature_lending_kyc_ic_otp_message
                    )
                )
            )
            delay(2000L)
            progressViewModel.showProgressSuccess(
                getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_otp_sent),
                BaseConstants.CDN_BASE_URL + SMALL_CHECK
            )
            progressViewModel.dismissGenericLoadingAfterMillis(2000L, true, FROM_SCREEN_CAPTCHA)
        }
    }
}
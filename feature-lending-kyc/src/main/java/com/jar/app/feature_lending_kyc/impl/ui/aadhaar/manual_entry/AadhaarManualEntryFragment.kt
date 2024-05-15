package com.jar.app.feature_lending_kyc.impl.ui.aadhaar.manual_entry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.data.event.LendingToolbarTitleEventV2
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.dp
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.textChanges
import com.jar.app.core_base.data.dto.getKycFeatureFlowType
import com.jar.app.core_base.data.dto.isFromLending
import com.jar.app.core_base.data.dto.isFromP2POrLending
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.widget.CustomEditTextWithErrorHandling
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycAadharManualEntryFragmentBinding
import com.jar.app.feature_lending_kyc.impl.data.Step
import com.jar.app.feature_lending_kyc.impl.domain.event.ToolbarStepsVisibilityEvent
import com.jar.app.feature_lending_kyc.impl.ui.aadhaar.captcha_bottomsheet.AadhaarCaptchaBottomSheet
import com.jar.app.feature_lending_kyc.impl.ui.loading.GenericLendingKycLoadingViewModel
import com.jar.app.feature_lending_kyc.impl.ui.loading.GenericLoadingArguments
import com.jar.app.feature_lending_kyc.impl.ui.otp.OtpSheetArguments
import com.jar.app.feature_lending_kyc.impl.ui.otp.OtpVerificationFragment.Companion.FROM_SCREEN_OTP
import com.jar.app.feature_lending_kyc.impl.util.AadhaarTextFormatter
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarActionPromptArgs
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarCaptcha
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarErrorScreenPrimaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarErrorScreenSecondaryButtonAction
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants.IllustrationUrls.AADHAAR_NOT_LINKED_WITH_NUMBER
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants.IllustrationUrls.AADHAAR_SERVER_DOWN_URL
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants.LottieUrls.SMALL_CHECK
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.app.feature_lending_kyc.shared.util.LendingKycFlowType
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class AadhaarManualEntryFragment :
    BaseFragment<FeatureLendingKycAadharManualEntryFragmentBinding>() {

    companion object {
        private const val MINIMUM_CAPTCHA_CHARACTER_ENTER = 3
        private const val FROM_SCREEN = "AadhaarManualEntryFragment"
        private const val ENTER_AADHAAR_MANUALLY_SCREEN = "Enter Aadhaar Manually Screen"
        private const val ENTER_AADHAAR_DETAIL_SCREEN = "Enter Aadhaar Details Screen"
        private const val AADHAAR_OTP_BOTTOM_SHEET = "Aadhaar OTP BottomSheet"
        private const val NO_MOBILE_LINKED_TO_AADHAAR_SCREEN = "No Mobile Linked To Aadhaar Screen"
        private const val RE_ENTER_CAPTCHA_BOTTOM_SHEET = "Re-enter Captcha BottomSheet"
        private const val SELFIE_AADHAAR_MISMATCH_SCREEN = "Selfie Aadhaar Mismatch Screen"
        private const val AADHAAR_EDIT_DETAILS_SCREEN = "Aadhaar Edit Details Screen"
        private const val AADHAAR_OTP_ATTEMPT_LIMIT_EXCEEDED_BOTTOM_SHEET =
            "Aadhaar OTP Attempt Limit Exceeded BottomSheet"
        private const val AADHAAR_OTP_EXPIRED_BOTTOM_SHEET = "Aadhaar OTP Expired BottomSheet"
        private const val CONFIRM = "Confirm"
        private const val TAKE_PHOTO = "Take photo"
        private const val REFRESH_CAPTCHA_ICON = "Refresh Captcha Icon"
        private const val AADHAAR_MANUAL_FLOW = "Aadhaar Manual flow"
        private const val INCORRECT_CAPTCHA = "incorrect_captcha"
        private const val INVALID_AADHARD_NUMBER = "invalid_aadhard_number"
        const val AADHAAR_VERIFYING_DETAILS_SCREEN = "Aadhaar Verifying Details Screen"
    }

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    private val viewModelProvider by viewModels<AadhaarManualEntryViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val progressViewModel by navGraphViewModels<GenericLendingKycLoadingViewModel>(R.id.feature_lending_kyc_steps_navigation)

    private var aadhaarCaptcha: AadhaarCaptcha? = null

    private val args: AadhaarManualEntryFragmentArgs by navArgs()

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                popBackStack()
            }
        }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycAadharManualEntryFragmentBinding
        get() = FeatureLendingKycAadharManualEntryFragmentBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupClickListener()
        observeLiveData()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        binding.aadharToolbar.tvTitle.text =
            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_aadhaar_details)
        binding.aadharToolbar.separator.isVisible = true
        EventBus.getDefault().post(
            ToolbarStepsVisibilityEvent(shouldShowSteps = false, Step.AADHAAR, true)
        )

        binding.btnConfirm.setDisabled(true)
        binding.customEditTextAadhaar.setEditTextEnumType(
            CustomEditTextWithErrorHandling.EditTextType.AADHAAR,
            uiScope
        )

        binding.etCaptcha.hint=" ${getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_captcha)}"
        analyticsHandler.postEvent(
            if ( getKycFeatureFlowType(args.kycFeatureFlowType).isFromLending()) LendingKycEventKey.Lending_AadharManualEntryScreenLaunched
            else LendingKycEventKey.Shown_EnterAadhaarDetailsScreen,
            mapOf(LendingKycEventKey.fromScreen to ENTER_AADHAAR_MANUALLY_SCREEN)
        )

        binding.customEditTextAadhaar.setTextWatcher(
            AadhaarTextFormatter(
                textColor = ContextCompat.getColor(
                    requireContext(),
                    com.jar.app.core_ui.R.color.white
                )
            )
        )
        if ( getKycFeatureFlowType(args.kycFeatureFlowType).isFromP2POrLending()) {
            binding.aadharToolbar.root.isGone = true
            binding.lendingFeatureGroup.isVisible = true
            Glide.with(requireContext())
                .load(BaseConstants.CDN_BASE_URL + LendingKycConstants.IllustrationUrls.AADHAAR_PLACEHOLDER_URL)
                .error(com.jar.app.core_ui.R.drawable.core_ui_ic_error)
                .into(binding.ivAadharCardImage)

        }
        EventBus.getDefault()
            .post(LendingToolbarTitleEventV2(getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_complete_kyc)))
        binding.etCaptcha.setOnFocusChangeListener { v, hasFocus ->
            binding.bottomPaddingView.isVisible = hasFocus
        }
    }

    private fun setupClickListener() {
        binding.ivRefreshCaptcha.setDebounceClickListener {
            sendEventForClickButton(REFRESH_CAPTCHA_ICON)
            refreshCaptcha(true)
        }
        binding.etCaptcha.textChanges()
            .debounce(100)
            .onEach {
                it?.let {
                    val isAadhaarValidated =
                        binding.customEditTextAadhaar.getRawText().length == CustomEditTextWithErrorHandling.AADHAAR_LENGTH
                    enableOrDisableConfirmButton(it.length < MINIMUM_CAPTCHA_CHARACTER_ENTER || isAadhaarValidated.not())
                    shouldShowCaptchaError(it.length < MINIMUM_CAPTCHA_CHARACTER_ENTER)
                    binding.ivCaptchaClear.isVisible = it.isNotEmpty()
                }
            }
            .launchIn(uiScope)
        binding.clTakePhoto.setDebounceClickListener {
            sendEventForClickButton(TAKE_PHOTO)
            navigateTo(
                AadhaarManualEntryFragmentDirections.actionToCaptureAadhaarPhotoFragment(
                    args.lenderName,
                    args.kycFeatureFlowType
                ),
                shouldAnimate = true
            )
        }
        binding.aadharToolbar.btnBack.setDebounceClickListener {
            EventBus.getDefault()
                .post(LendingBackPressEvent(LendingKycEventKey.AADHAR_MANUAL_ENTRY_SCREEN))
            popBackStack()
        }
        binding.btnConfirm.setDebounceClickListener {
            sendEventForClickButton(CONFIRM)
            analyticsHandler.postEvent(
                LendingKycEventKey.OTP_SENT,
                mapOf(
                    LendingKycEventKey.isFromLendingFlow to  getKycFeatureFlowType(args.kycFeatureFlowType).isFromLending()
                )
            )
            viewModel.requestAadhaarOtp(
                binding.customEditTextAadhaar.getRawText(),
                binding.etCaptcha.text.toString(),
                viewModelProvider.aadhaarCaptcha?.sessionId ?: "",
                getKycFeatureFlowType(args.kycFeatureFlowType)
            )
        }
        binding.customEditTextAadhaar.setDebounceClickListener {
            if ( getKycFeatureFlowType(args.kycFeatureFlowType).isFromLending()) {
                analyticsHandler.postEvent(
                    LendingKycEventKey.Lending_ENTRY_FIELD_SELECTED,
                    mapOf(
                        LendingKycEventKey.field_name to binding.tvEnterAadhaarNumber.text.toString()
                    )
                )
            }
        }
        binding.etCaptcha.setDebounceClickListener {
            if ( getKycFeatureFlowType(args.kycFeatureFlowType).isFromLending()) {
                analyticsHandler.postEvent(
                    LendingKycEventKey.Lending_ENTRY_FIELD_SELECTED,
                    mapOf(
                        LendingKycEventKey.field_name to binding.tvCaptchaError.text.toString()
                    )
                )
            }
        }
        binding.customEditTextAadhaar.setIsValidatedListener { isValid, _ ->
            if ( getKycFeatureFlowType(args.kycFeatureFlowType).isFromLending())
                analyticsHandler.postEvent(
                    LendingKycEventKey.Lending_AADHAR_NUMBER_ENTRY,
                    mapOf(
                        LendingKycEventKey.status to isValid.toString()
                    )
                )
            enableOrDisableConfirmButton(
                !isValid ||
                        binding.etCaptcha.text?.length.orZero() < MINIMUM_CAPTCHA_CHARACTER_ENTER
            )
        }
        binding.ivCaptchaClear.setDebounceClickListener {
            binding.etCaptcha.setText("")
        }

    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.captchaResponseFlow.collect(
                    onLoading = {
                        shouldShowCaptchaProgress(true)
                    },
                    onSuccess = {
                        dismissProgressBar()
                        aadhaarCaptcha = it
                        it?.let {
                            shouldShowCaptchaProgress(false)
                            viewModelProvider.getCaptchaImage(it)
                        }
                    },
                    onError = { message, errorCode ->
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
        }
        progressViewModel.genericLoadingOnAutoDismissViewModel.observe(viewLifecycleOwner) {
            if (it.isDismissingAfterSuccess) {
                when (it.fromScreen) {
                    FROM_SCREEN, AadhaarCaptchaBottomSheet.FROM_SCREEN_CAPTCHA -> {
                        val args = encodeUrl(
                            serializer.encodeToString(
                                OtpSheetArguments(
                                    flowType = LendingKycConstants.LendingKycOtpVerificationFlowType.AADHAAR,
                                    expiresInTime = 30L,
                                    resendTime = 30L,
                                    email = null,
                                    aadhaarSessionId = aadhaarCaptcha?.sessionId,
                                    aadhaarNumber = binding.customEditTextAadhaar.getRawText(),
                                    emailMessageId = null,
                                    fromScreen = AADHAAR_MANUAL_FLOW,
                                    lenderName = args.lenderName,
                                    kycFeatureFlowType = getKycFeatureFlowType(args.kycFeatureFlowType)
                                )
                            )
                        )
                        navigateTo(
                            FeatureLendingKycStepsNavigationDirections.actionToOtpVerificationFragment(
                                args
                            ),
                            true,
                            popUpTo = R.id.aadhaarManualEntryFragment,
                            inclusive = false
                        )
                    }

                    FROM_SCREEN_OTP -> {
                        navigateTo(
                            AadhaarManualEntryFragmentDirections.actionToSuccessStepDialog(
                                flowType = LendingKycFlowType.AADHAAR,
                                fromScreen = AADHAAR_VERIFYING_DETAILS_SCREEN,
                                lenderName = args.lenderName,
                                kycFeatureFlowType = args.kycFeatureFlowType
                            ),
                            popUpTo = R.id.aadhaarManualEntryFragment,
                            inclusive = true
                        )
                    }
                }
            }
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
                                BaseConstants.ErrorCodesLendingKyc.Aadhaar.INVALID_AADHAAR -> { //Invalid Aadhaar
                                    binding.customEditTextAadhaar.showError(it.errorMessage.orEmpty())
                                    refreshCaptcha(true)
                                    analyticsHandler.postEvent(
                                        LendingKycEventKey.Shown_EnterAadhaarDetailsScreenEdgeCases,
                                        mapOf(LendingKycEventKey.fromScreen to it.errorMessage.orEmpty())
                                    )
                                    if ( getKycFeatureFlowType(args.kycFeatureFlowType).isFromLending()) {
                                        analyticsHandler.postEvent(
                                            LendingKycEventKey.Lending_AadharManualEntryScreenError,
                                            mapOf(LendingKycEventKey.errorType to INVALID_AADHARD_NUMBER)
                                        )
                                    }
                                }

                                BaseConstants.ErrorCodesLendingKyc.Aadhaar.INVALID_CAPTCHA -> { //Invalid captcha
                                    if ( getKycFeatureFlowType(args.kycFeatureFlowType).isFromLending()) {
                                        analyticsHandler.postEvent(
                                            LendingKycEventKey.Lending_AadharManualEntryScreenError,
                                            mapOf(LendingKycEventKey.errorType to INCORRECT_CAPTCHA)
                                        )
                                    }
                                    shouldShowCaptchaError(true, it.errorMessage)
                                    refreshCaptcha(false)
                                    analyticsHandler.postEvent(
                                        LendingKycEventKey.Shown_EnterAadhaarDetailsScreenEdgeCases,
                                        mapOf(LendingKycEventKey.fromScreen to it.errorMessage.orEmpty())
                                    )
                                }

                                BaseConstants.ErrorCodesLendingKyc.Aadhaar.NO_MOBILE_LINKED -> { //Aadhaar number doesn't have mobile number
                                    analyticsHandler.postEvent(
                                        LendingKycEventKey.Shown_NoMobileLinkedToAadhaarScreen,
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
                                                contactMessage = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_my_aadhaar_is_not_linked_help_me),
                                                kycFeatureFlowType = getKycFeatureFlowType(args.kycFeatureFlowType)
                                            )
                                        )
                                    )
                                }

                                BaseConstants.ErrorCodesLendingKyc.Aadhaar.UNABLE_TO_REACH_UIDAI -> { //Unable to reach Aadhaar UIDAI at the moment
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
                                                kycFeatureFlowType = getKycFeatureFlowType(args.kycFeatureFlowType)
                                            )
                                        )
                                    )
                                }

                                else -> {
                                    it.errorMessage?.let {
                                        it.snackBar(binding.root)
                                        refreshCaptcha(true)
                                    }
                                }
                            }

                        }
                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }

        uiScope.launch {
            findNavController().currentBackStackEntryFlow.collectLatest {
                if (it.destination.id == R.id.aadhaarCaptchaBottomSheet || it.destination.id == R.id.otpVerificationFragment) {
                    updateStateOnResume()
                }
            }
        }
    }

    private fun refreshCaptcha(shouldClearPreviousCaptcha: Boolean = false) {
        viewModel.fetchAadhaarCaptcha(getKycFeatureFlowType(args.kycFeatureFlowType))
        if (shouldClearPreviousCaptcha) {
            binding.etCaptcha.setText("")
        }
    }

    private fun enableOrDisableConfirmButton(shouldDisable: Boolean = false) {
        if (getKycFeatureFlowType(args.kycFeatureFlowType).isFromP2POrLending()) { //true || false || false
            val isCaptchaValidated =
                binding.etCaptcha.text?.length.orZero() >= MINIMUM_CAPTCHA_CHARACTER_ENTER
            val isAadhaarValidated =
                binding.customEditTextAadhaar.getRawText().length == CustomEditTextWithErrorHandling.AADHAAR_LENGTH
            binding.btnConfirm.setDisabled(!isCaptchaValidated || !isAadhaarValidated)
        } else {
            binding.btnConfirm.setDisabled(shouldDisable)
        }

    }

    private fun shouldShowCaptchaError(shouldShow: Boolean, errorMessage: String? = null) {
        binding.tvCaptchaError.isVisible = shouldShow && errorMessage != null
        binding.tvCaptchaError.text = errorMessage.orEmpty()
        binding.etCaptcha.background =
            ContextCompat.getDrawable(
                requireContext(),
                if (shouldShow && errorMessage != null) com.jar.app.core_ui.R.drawable.core_ui_bg_rounded_2e2942_outline_eb6a6e_10dp
                else com.jar.app.core_ui.R.drawable.core_ui_bg_rounded_2e2942_10dp
            )
    }

    private fun shouldShowCaptchaProgress(shouldShow: Boolean) {
        binding.pbCaptcha.isVisible = shouldShow
        binding.ivRefreshCaptcha.isInvisible = shouldShow
    }

    private fun sendEventForClickButton(optionChosen: String) {
        analyticsHandler.postEvent(
            LendingKycEventKey.Clicked_Button_EnterAadhaarDetailsScreen,
            mapOf(
                LendingKycEventKey.optionChosen to optionChosen,
                LendingKycEventKey.isFromLendingFlow to  getKycFeatureFlowType(args.kycFeatureFlowType).isFromLending()
            )
        )
    }

    private fun simulateOtpSentProgress() {
        uiScope.launch {
            analyticsHandler.postEvent(
                LendingKycEventKey.Shown_AadhaarSendingOTPScreen,
                mapOf(LendingKycEventKey.fromScreen to ENTER_AADHAAR_DETAIL_SCREEN)
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
            delay(1000L)
            progressViewModel.showProgressSuccess(
                getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_otp_sent),
                BaseConstants.CDN_BASE_URL + SMALL_CHECK
            )
            progressViewModel.dismissGenericLoadingAfterMillis(
                2000L,
                true,
                FROM_SCREEN
            )
        }
    }

    override fun onResume() {
        super.onResume()
        updateStateOnResume()
    }

    private fun updateStateOnResume() {
        refreshCaptcha(true)
        binding.btnConfirm.setDisabled(true)
        binding.customEditTextAadhaar.setCharacterCount(
            binding.customEditTextAadhaar.getRawText().length,
            CustomEditTextWithErrorHandling.AADHAAR_LENGTH
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
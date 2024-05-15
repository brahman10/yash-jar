package com.jar.app.feature_lending_kyc.impl.ui.selfie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.data.event.LendingToolbarTitleEventV2
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.core_base.data.dto.isFromLending
import com.jar.app.core_base.data.dto.isFromP2POrLending
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_image_picker.api.ImagePickerManager
import com.jar.app.core_image_picker.api.data.CameraType
import com.jar.app.core_image_picker.api.data.ImagePickerOption
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_kyc.shared.domain.model.DocType
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycFragmentSelfieCheckBinding
import com.jar.app.feature_lending_kyc.impl.data.Step
import com.jar.app.feature_lending_kyc.impl.domain.event.ToolbarStepsVisibilityEvent
import com.jar.app.feature_lending_kyc.impl.domain.model.KYCScreenArgs
import com.jar.app.feature_lending_kyc.impl.ui.aadhaar.confirmation.AadhaarConfirmationFragmentDirections
import com.jar.app.feature_lending_kyc.impl.ui.aadhaar.manual_entry.AadhaarManualEntryFragmentDirections
import com.jar.app.feature_lending_kyc.impl.ui.loading.GenericLendingKycLoadingViewModel
import com.jar.app.feature_lending_kyc.impl.ui.loading.GenericLoadingArguments
import com.jar.app.feature_lending_kyc.impl.ui.steps.LendingKycStepsViewModel
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarActionPromptArgs
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarErrorScreenPrimaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarErrorScreenSecondaryButtonAction
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants.LottieUrls.GENERIC_LOADING
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants.LottieUrls.SMALL_CHECK
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.app.feature_lending_kyc.shared.util.LendingKycFlowType
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class SelfieCheckFragment : BaseFragment<FeatureLendingKycFragmentSelfieCheckBinding>() {

    @Inject
    lateinit var imagePickerManager: ImagePickerManager

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsHandler: AnalyticsApi
    private val arguments: SelfieCheckFragmentArgs by navArgs()
    private val args by lazy {
        serializer.decodeFromString<KYCScreenArgs>(decodeUrl(arguments.screenArgs))
    }
    private val viewModelProvider: SelfieCheckViewModelAndroid by viewModels { defaultViewModelProviderFactory }
    private val viewModel by lazy { viewModelProvider.getInstance() }

    private val lendingKycStepsViewModel: LendingKycStepsViewModel by activityViewModels()

    private val loadingViewModel by navGraphViewModels<GenericLendingKycLoadingViewModel>(R.id.feature_lending_kyc_steps_navigation)
    private var retries = 0

    companion object {
        private const val FROM_SCREEN = "SelfieCheckFragment"
        private const val SELFIE_PREREQUISITES_SCREEN = "Selfie Prerequisites Screen"
        private const val AADHAAR_VERIFICATION_SUCCESSFUL_SCREEN =
            "Aadhaar Verification Successful Screen"
        private const val CONTINUE_KYC_BOTTOM_SHEET = "Continue KYC BottomSheet"
        private const val VERIFYING_SELFIE_SCREEN = "Verifying Selfie Screen"
        private const val SELFIE_FLOW = "Selfie Flow"
        private const val DELAY_IN_REDIRECTION = 2000L
        private const val DELAY_AFTER_ERROR = 1L
        private const val RETRIES_LIMIT = 5
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (args.kycFeatureFlowType.isFromP2POrLending()) {
                    EventBus.getDefault()
                        .post(
                            LendingBackPressEvent(
                                screenName = LendingKycEventKey.SELFIE_LAUNCH_SCREEN,
                                shouldNavigateBack = true,
                                popupId = R.id.selfieCheckFragment
                            )
                        )
                } else {
                    lendingKycStepsViewModel.viewOnlyNavigationRedirectTo(
                        LendingKycFlowType.SELFIE,
                        false,
                        WeakReference(requireActivity())
                    )
                }
            }
        }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycFragmentSelfieCheckBinding
        get() = FeatureLendingKycFragmentSelfieCheckBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(ToolbarNone)
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUi()
        setupClickListener()
        observeLiveData()
        EventBus.getDefault().post(
            ToolbarStepsVisibilityEvent(
                shouldShowSteps = true,
                Step.SELFIE
            )
        )
        registerBackPressDispatcher()
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.selfieUploadRequestFlow.collectUnwrapped(
                    onLoading = {
                        analyticsHandler.postEvent(
                            LendingKycEventKey.Shown_PhotoUploadScreen,
                            mapOf(LendingKycEventKey.scenario to SELFIE_FLOW)
                        )
                        navigateTo(
                            SelfieCheckFragmentDirections.actionToGenericLoadingDialog(
                                GenericLoadingArguments(
                                    title = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_uploading_selfie),
                                    description = null,
                                    assetsUrl = BaseConstants.CDN_BASE_URL + GENERIC_LOADING,
                                    illustrationResourceId = null
                                )
                            )
                        )
                    },
                    onSuccess = {
                       if (it.success){
                           loadingViewModel.showProgressSuccess(
                               getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_verifying_selfie),
                               BaseConstants.CDN_BASE_URL + SMALL_CHECK
                           )
                           loadingViewModel.dismissGenericLoadingAfterMillis(
                               2000L,
                               true,
                               FROM_SCREEN
                           )
                       }else{
                           onErrorInSelfieUpload(errorMessage = it.errorMessage.orEmpty(),errorCode = it.errorCode?.toString())
                       }
                    },
                    onError = { errorMessage, errorCode ->
                        onErrorInSelfieUpload(errorMessage, errorCode)
                    }
                )
            }
        }
        loadingViewModel.genericLoadingOnAutoDismissViewModel.observe(viewLifecycleOwner) {
            if (it.isDismissingAfterSuccess && it.fromScreen == FROM_SCREEN) { // loading dialog dismissed go to next
                //Shown_SelfieVerificationSuccessfulScreen
                analyticsHandler.postEvent(
                    LendingKycEventKey.Shown_SelfieVerificationSuccessfulScreen,
                    mapOf(LendingKycEventKey.fromScreen to VERIFYING_SELFIE_SCREEN)
                )
                navigateTo(
                    AadhaarConfirmationFragmentDirections.actionToSuccessStepDialog(
                        flowType = LendingKycFlowType.SELFIE,
                        fromScreen = VERIFYING_SELFIE_SCREEN,
                        lenderName = args.lenderName,
                        kycFeatureFlowType = args.kycFeatureFlowType.name
                    )
                )
            }
        }

    }

    private fun onErrorInSelfieUpload(errorMessage:String, errorCode:String?){
        loadingViewModel.updateGenericLoadingTitle(errorMessage)
        loadingViewModel.dismissGenericLoadingAfterMillis(10L, false, FROM_SCREEN)
        analyticsHandler.postEvent(
            LendingKycEventKey.Shown_SelfieEdgeCaseScreens,
            mapOf(LendingKycEventKey.textDisplayed to errorMessage)
        )
        when (errorCode) { //Handle error states.
            BaseConstants.ErrorCodesLendingKyc.Selfie.NO_IMAGE_FOUND -> { //No image found
                navigateTo(
                    SelfieCheckFragmentDirections.actionToSelfieEdgeCaseFragment(
                        BaseConstants.CDN_BASE_URL +
                                LendingKycConstants.IllustrationUrls.SELFIE_NO_IMAGE_FOUND,
                        getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_no_image_found)
                    )
                )
            }

            BaseConstants.ErrorCodesLendingKyc.Selfie.FACE_NOT_DETECTED -> {//Face not detected
                navigateTo(
                    SelfieCheckFragmentDirections.actionToSelfieEdgeCaseFragment(
                        BaseConstants.CDN_BASE_URL +
                                LendingKycConstants.IllustrationUrls.SELFIE_FACE_NOT_DETECTED,
                        getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_face_not_detected)
                    )
                )
            }

            BaseConstants.ErrorCodesLendingKyc.Selfie.EYE_IS_CLOSED -> {//Eye is closed
                navigateTo(
                    SelfieCheckFragmentDirections.actionToSelfieEdgeCaseFragment(
                        BaseConstants.CDN_BASE_URL +
                                LendingKycConstants.IllustrationUrls.SELFIE_EYES_CLOSED,
                        getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_eyes_are_closed)
                    )
                )
            }

            BaseConstants.ErrorCodesLendingKyc.Selfie.LOW_QUALITY_IMAGE -> {
                navigateTo(
                    SelfieCheckFragmentDirections.actionToSelfieEdgeCaseFragment(
                        BaseConstants.CDN_BASE_URL +
                                LendingKycConstants.IllustrationUrls.SELFIE_LOW_QUALITY,
                        getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_low_queality_image)
                    )
                )
            }

            BaseConstants.ErrorCodesLendingKyc.Selfie.SELFIE_MATCH_FAILED -> {
                navigateTo(
                    SelfieCheckFragmentDirections.actionToAadhaarActionPromptFragment(
                        AadhaarActionPromptArgs(
                            assetsUrl = BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                            titleText = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_uh_oh_exclamation),
                            subtitleText = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_your_selfie_does_not_match),
                            primaryActionText = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_retake_selfie),
                            secondaryActionText = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_),
                            primaryButtonAction = AadhaarErrorScreenPrimaryButtonAction.GO_BACK,
                            secondaryButtonAction = AadhaarErrorScreenSecondaryButtonAction.NONE,
                            isIllustrationUrl = false,
                            kycFeatureFlowType = args.kycFeatureFlowType
                        )
                    )
                )
            }

            BaseConstants.ErrorCodesLendingKyc.Selfie.SELFIE_MATCH_RETRY_LIMIT_EXCEEDED -> {
                navigateTo(
                    SelfieCheckFragmentDirections.actionToAadhaarActionPromptFragment(
                        AadhaarActionPromptArgs(
                            assetsUrl = BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                            titleText = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_verification_attempt_limit_exceeded),
                            subtitleText = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_please_try_again_tomorrow),
                            primaryActionText = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_back_to_home),
                            secondaryActionText = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_contact_support),
                            primaryButtonAction = AadhaarErrorScreenPrimaryButtonAction.GO_HOME,
                            secondaryButtonAction = AadhaarErrorScreenSecondaryButtonAction.CONTACT_SUPPORT,
                            isIllustrationUrl = false,
                            contactMessage = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_my_selfie_limit_exceeded),
                            kycFeatureFlowType = args.kycFeatureFlowType
                        )
                    )
                )
            }

            BaseConstants.ErrorCodesLendingKyc.Selfie.SELFIE_MATCH_RETRY_LIMIT_EXHAUSTED -> {
                navigateTo(
                    SelfieCheckFragmentDirections.actionToAadhaarActionPromptFragment(
                        AadhaarActionPromptArgs(
                            assetsUrl = BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                            titleText = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_uh_oh_exclamation),
                            subtitleText = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_verification_was_not_successful_please_connect_with_support),
                            primaryActionText = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_back_to_home),
                            secondaryActionText = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_contact_support),
                            primaryButtonAction = AadhaarErrorScreenPrimaryButtonAction.GO_HOME,
                            secondaryButtonAction = AadhaarErrorScreenSecondaryButtonAction.CONTACT_SUPPORT,
                            isIllustrationUrl = false,
                            contactMessage = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_my_selfie_limit_exhausted),
                            kycFeatureFlowType = args.kycFeatureFlowType
                        )
                    )
                )
            }

            else -> {
                onErrorOccurred()
            }
        }
    }
    private fun onErrorOccurred() {
        loadingViewModel.dismissGenericLoadingAfterMillis(
            0L, false,
            FROM_SCREEN
        )
        retries++
        showUploadingFailedScreen(retries >= RETRIES_LIMIT)
    }

    private fun showUploadingFailedScreen(isTooManyAttempts: Boolean) {
        val description =
            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_your_selfie_was_not_uploaded)
        navigateTo(
            AadhaarManualEntryFragmentDirections.actionToAadhaarActionPromptFragment(
                AadhaarActionPromptArgs(
                    BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                    getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_oops_upload_failed),
                    description,
                    getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_retry_uploading),
                    if (isTooManyAttempts) getString(com.jar.app.core_ui.R.string.contact_us) else "",
                    AadhaarErrorScreenPrimaryButtonAction.GO_BACK,
                    AadhaarErrorScreenSecondaryButtonAction.CONTACT_SUPPORT,
                    contactMessage = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_my_selfie_is_not_getting_uploaded),
                    kycFeatureFlowType = KycFeatureFlowType.LENDING
                )
            ),
            shouldAnimate = true
        )
        analyticsHandler.postEvent(
            LendingKycEventKey.Shown_SelfieUploadFailedScreens,
            mapOf(LendingKycEventKey.textDisplayed to description)

        )
    }

    private fun setupClickListener() {
        binding.btnTakeSelfie.setDebounceClickListener {
            EventBus.getDefault().post(
                ToolbarStepsVisibilityEvent(
                    shouldShowSteps = false,
                    Step.SELFIE
                )
            )
            analyticsHandler.postEvent(
                LendingKycEventKey.Shown_SelfieCaptureScreen,
                mapOf(LendingKycEventKey.fromScreen to SELFIE_PREREQUISITES_SCREEN)
            )
            if (args.kycFeatureFlowType.isFromLending()) {
                analyticsHandler.postEvent(
                    LendingKycEventKey.Lending_SelfieScreenClicked,
                    mapOf(LendingKycEventKey.action to "take_selfie_clicked")
                )
            }
            imagePickerManager.openImagePicker(
                ImagePickerOption(cameraType = CameraType.SELFIE, docType = DocType.DEFAULT.name),
                findNavController(),
                SELFIE_FLOW,
                args.kycFeatureFlowType
            ) {
                //remove all the screen until this screen
                popBackStack(R.id.selfieCheckFragment, false)
                EventBus.getDefault().post(
                    ToolbarStepsVisibilityEvent(
                        shouldShowSteps = true,
                        Step.SELFIE
                    )
                )
                viewModel.uploadSelfie(File(it).readBytes(), args.kycFeatureFlowType, args.applicationId)
            }
        }
    }

    private fun setupUi() {
        Glide.with(requireContext())
            .load(BaseConstants.CDN_BASE_URL + LendingKycConstants.IllustrationUrls.CLEAR_FACE_SELFIE_URL)
            .into(binding.ivFirst)
        Glide.with(requireContext())
            .load(BaseConstants.CDN_BASE_URL + LendingKycConstants.IllustrationUrls.MASKED_FACE_SELFIE_URL)
            .into(binding.ivSecond)
        Glide.with(requireContext())
            .load(BaseConstants.CDN_BASE_URL + LendingKycConstants.IllustrationUrls.BLURRED_FACE_SELFIE_URL)
            .into(binding.ivThird)

        analyticsHandler.postEvent(
            LendingKycEventKey.Shown_SelfiePrerequisitesScreen,
            mapOf(LendingKycEventKey.fromScreen to AADHAAR_VERIFICATION_SUCCESSFUL_SCREEN)
        )
        if (args.kycFeatureFlowType.isFromLending()) {
            analyticsHandler.postEvent(
                LendingKycEventKey.Lending_SelfieScreenLaunched,
                mapOf(LendingKycEventKey.fromScreen to BaseConstants.FROM_LENDING)
            )
        }
        EventBus.getDefault()
            .post(LendingToolbarTitleEventV2(getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_complete_kyc)))

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
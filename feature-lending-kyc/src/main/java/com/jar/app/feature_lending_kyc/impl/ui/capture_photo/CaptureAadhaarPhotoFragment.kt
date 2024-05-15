package com.jar.app.feature_lending_kyc.impl.ui.capture_photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.data.event.LendingToolbarVisibilityEventV2
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_image_picker.api.ImagePickerManager
import com.jar.app.core_image_picker.api.data.ImagePickerOption
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_kyc.shared.domain.model.DocType
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycFragmentCaptureAadhaarPhotoBinding
import com.jar.app.feature_lending_kyc.impl.data.Step
import com.jar.app.feature_lending_kyc.impl.domain.event.ToolbarStepsVisibilityEvent
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarErrorScreenPrimaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarErrorScreenSecondaryButtonAction
import com.jar.app.feature_lending_kyc.impl.ui.aadhaar.confirmation.AadhaarConfirmationFragment
import com.jar.app.feature_lending_kyc.impl.ui.aadhaar.manual_entry.AadhaarManualEntryFragmentDirections
import com.jar.app.feature_lending_kyc.impl.ui.loading.GenericLendingKycLoadingViewModel
import com.jar.app.feature_lending_kyc.impl.ui.loading.GenericLoadingArguments
import com.jar.app.core_base.data.dto.getKycFeatureFlowType
import com.jar.app.core_base.data.dto.isFromLending
import com.jar.app.core_base.data.dto.isFromP2POrLending
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarActionPromptArgs
import com.jar.app.feature_lending_kyc.shared.domain.model.KycAadhaar
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants.IllustrationUrls.AADHAAR_CARD_BLURRED_URL
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants.IllustrationUrls.AADHAAR_CARD_IS_NOT_IN_FRAME
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants.IllustrationUrls.AADHAAR_CARD_NOT_DETECTED_URL
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey.Shown_OCRPhotoUploadErrorScreen
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class CaptureAadhaarPhotoFragment :
    BaseFragment<FeatureLendingKycFragmentCaptureAadhaarPhotoBinding>() {

    @Inject
    lateinit var imagePickerManager: ImagePickerManager

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args:CaptureAadhaarPhotoFragmentArgs by navArgs()

    private val viewModel by viewModels<CaptureDocumentPhotoViewModel> { defaultViewModelProviderFactory }

    private val loadingViewModel by navGraphViewModels<GenericLendingKycLoadingViewModel>(R.id.feature_lending_kyc_steps_navigation)

    private var ocrAadhaarData: KycAadhaar? = null

    private var retries = 0

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                popBackStack()
                EventBus.getDefault()
                    .post(
                        LendingBackPressEvent(
                            LendingKycEventKey.AADHAR_OCR_FIRST_SCREEN,
                            shouldNavigateBack = false
                        )
                    )
            }
        }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycFragmentCaptureAadhaarPhotoBinding
        get() = FeatureLendingKycFragmentCaptureAadhaarPhotoBinding::inflate

    companion object {
        private const val DELAY_IN_REDIRECTION = 2000L
        private const val DELAY_AFTER_ERROR = 1L
        private const val RETRIES_LIMIT = 5
        private const val BACK_ARROW = "Back Arrow"
        private const val FROM_SCREEN = "CaptureAadhaarPhotoFragment"
        private const val AADHAAR_OCR_FLOW = "Aadhaar OCR Flow"
    }

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
        analyticsHandler.postEvent(
            LendingKycEventKey.Shown_OCROptionScreen,
            mapOf(
                LendingKycEventKey.scenario to AADHAAR_OCR_FLOW,
                LendingKycEventKey.isFromLendingFlow to  getKycFeatureFlowType(args.kycFeatureFlowType).isFromLending()
            )
        )
        binding.captureToolbar.tvTitle.text = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_capture_photo)
        binding.captureToolbar.separator.isVisible = true
        EventBus.getDefault().post(
            ToolbarStepsVisibilityEvent(
                shouldShowSteps = false,
                Step.AADHAAR
            )
        )
        Glide.with(requireContext())
            .load(BaseConstants.CDN_BASE_URL + AADHAAR_CARD_NOT_DETECTED_URL)
            .into(binding.ivInnerFirstIllustration)
        Glide.with(requireContext())
            .load(BaseConstants.CDN_BASE_URL + AADHAAR_CARD_IS_NOT_IN_FRAME)
            .into(binding.ivInnerSecondIllustration)
        Glide.with(requireContext())
            .load(BaseConstants.CDN_BASE_URL + AADHAAR_CARD_BLURRED_URL)
            .into(binding.ivInnerThirdIllustration)

        if (getKycFeatureFlowType(args.kycFeatureFlowType).isFromP2POrLending()) {
            binding.captureToolbar.root.isVisible = false
        }

    }

    private fun setupClickListener() {
        binding.btnOpenCamera.setDebounceClickListener {
            analyticsHandler.postEvent(
                LendingKycEventKey.Clicked_Button_OCROptionScreen,
                mapOf(
                    LendingKycEventKey.scenario to AADHAAR_OCR_FLOW,
                    LendingKycEventKey.optionChosen to binding.btnOpenCamera.getText(),
                    LendingKycEventKey.isFromLendingFlow to getKycFeatureFlowType(args.kycFeatureFlowType).isFromLending()
                )
            )

            imagePickerManager.openImagePicker(
                ImagePickerOption(docType = DocType.AADHAAR.name),
                findNavController(),
                AADHAAR_OCR_FLOW,
                getKycFeatureFlowType(args.kycFeatureFlowType)
            ) {
                viewModel.postDocumentOcrRequest(DocType.AADHAAR.name, it)
                popBackStack(R.id.aadhaarCapturePhotoFragment, false)
            }
        }
        binding.captureToolbar.btnBack.setDebounceClickListener {
            analyticsHandler.postEvent(
                LendingKycEventKey.Clicked_Button_OCROptionScreen,
                mapOf(
                    LendingKycEventKey.scenario to AADHAAR_OCR_FLOW,
                    LendingKycEventKey.optionChosen to BACK_ARROW
                )
            )
            popBackStack()
        }
    }

    private fun observeLiveData() {
        viewModel.documentOcrRequestLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                analyticsHandler.postEvent(
                    LendingKycEventKey.Shown_PhotoUploadScreen,
                    mapOf(LendingKycEventKey.scenario to AADHAAR_OCR_FLOW)
                )
                navigateTo(
                    CaptureAadhaarPhotoFragmentDirections.actionToGenericLoadingDialog(
                        GenericLoadingArguments(
                            title = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_uploading_photo),
                            description = null,
                            assetsUrl = BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_LOADING,
                            illustrationResourceId = null
                        )
                    ),
                    true
                )
            },
            onSuccess = {
                it?.let {
                    if (it.errorMsg != null) {
                        loadingViewModel.dismissGenericLoadingAfterMillis(
                            DELAY_AFTER_ERROR,
                            false,
                            FROM_SCREEN
                        )
                        navigateTo(
                            CaptureAadhaarPhotoFragmentDirections.actionToAadhaarUploadFailedFragment(
                                it
                            )
                        )
                    } else {
                        ocrAadhaarData =
                            KycAadhaar(it.getDocNumber(), it.dob ?: it.yob, it.name)
                        loadingViewModel.updateGenericLoadingTitle(getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_fetching_your_aadhar_detail))
                        loadingViewModel.dismissGenericLoadingAfterMillis(
                            DELAY_IN_REDIRECTION,
                            true,
                            FROM_SCREEN
                        )
                    }
                }
            },
            onError = {
                onErrorOccurred()
            }
        )
        loadingViewModel.genericLoadingOnAutoDismissViewModel.observe(viewLifecycleOwner) {
            if (it.isDismissingAfterSuccess && it.fromScreen == FROM_SCREEN) {
                // loading dialog dismissed go to next
                ocrAadhaarData?.let {
                    navigateTo(
                        CaptureAadhaarPhotoFragmentDirections.actionToAadhaarConfirmationFragment(
                            aadhaarDetail = it,
                            lenderName = args.lenderName,
                            flowType = AadhaarConfirmationFragment.FLOW_AADHAAR_UPLOAD,
                            kycFeatureFlowType = args.kycFeatureFlowType
                        )
                    )
                }
            }
        }
    }

    private fun onErrorOccurred() {
        loadingViewModel.dismissGenericLoadingAfterMillis(0L, false, FROM_SCREEN)
        retries++
        showUploadingFailedScreen(retries >= RETRIES_LIMIT)
    }

    private fun showUploadingFailedScreen(isTooManyAttempts: Boolean) {
        val description = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_your_aadhar_was_not_uploaded)
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
                    contactMessage = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_my_aadhaar_photo_is_not_getting_uploaded),
                    kycFeatureFlowType = getKycFeatureFlowType(args.kycFeatureFlowType)
                )
            ),
            true
        )
        analyticsHandler.postEvent(
            Shown_OCRPhotoUploadErrorScreen,
            mapOf(LendingKycEventKey.textDisplayed to description)

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

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().post(LendingToolbarVisibilityEventV2(false))
    }
}
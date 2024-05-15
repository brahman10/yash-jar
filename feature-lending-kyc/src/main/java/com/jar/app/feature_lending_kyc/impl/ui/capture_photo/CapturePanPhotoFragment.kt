package com.jar.app.feature_lending_kyc.impl.ui.capture_photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.encodeUrl
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_image_picker.api.ImagePickerManager
import com.jar.app.core_image_picker.api.data.ImagePickerOption
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_kyc.shared.domain.model.DocType
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycFragmentCapturePanPhotoBinding
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenPrimaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenSecondaryButtonAction
import com.jar.app.feature_lending_kyc.impl.ui.loading.GenericLendingKycLoadingViewModel
import com.jar.app.feature_lending_kyc.impl.ui.loading.GenericLoadingArguments
import com.jar.app.feature_lending_kyc.impl.ui.pan.error_screens.PanErrorStatesArguments
import com.jar.app.feature_lending_kyc.shared.domain.arguments.CreditReportScreenArguments
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportPAN
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class CapturePanPhotoFragment :
    BaseFragment<FeatureLendingKycFragmentCapturePanPhotoBinding>() {

    @Inject
    lateinit var imagePickerManager: ImagePickerManager

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args:CapturePanPhotoFragmentArgs by navArgs()
    private val viewModel by viewModels<CaptureDocumentPhotoViewModel> { defaultViewModelProviderFactory }

    private val loadingViewModel by navGraphViewModels<GenericLendingKycLoadingViewModel>(R.id.feature_lending_kyc_steps_navigation)

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycFragmentCapturePanPhotoBinding
        get() = FeatureLendingKycFragmentCapturePanPhotoBinding::inflate

    private var creditReportPAN: CreditReportPAN? = null

    companion object {
        private const val BACK_ARROW = "Back Arrow"
        private const val FROM_SCREEN = "CapturePanPhotoFragment"
        private const val PAN_OCR_FLOW = "PAN OCR Flow"
        private const val FETCH_PAN_DETAILS_FROM_OCR_SCREEN = "Fetch PAN Details From OCR Screen"
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupClickListener()
        observeLiveData()
    }

    private fun setupUI() {
        analyticsHandler.postEvent(
            LendingKycEventKey.Shown_OCROptionScreen,
            mapOf(
                LendingKycEventKey.scenario to PAN_OCR_FLOW
            )
        )
        binding.toolbar.tvTitle.text = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_capture_photo)
        Glide.with(requireContext())
            .load(BaseConstants.CDN_BASE_URL + LendingKycConstants.IllustrationUrls.PAN_CARD_NOT_DETECTED_URL)
            .into(binding.ivInnerFirstIllustration)
        Glide.with(requireContext())
            .load(BaseConstants.CDN_BASE_URL + LendingKycConstants.IllustrationUrls.PAN_CARD_IS_NOT_IN_FRAME)
            .into(binding.ivInnerSecondIllustration)
        Glide.with(requireContext())
            .load(BaseConstants.CDN_BASE_URL + LendingKycConstants.IllustrationUrls.PAN_IS_BLURRED_URL)
            .into(binding.ivInnerThirdIllustration)

        if (args.shouldInitiateCameraDirectly)
            openImagePickerModule()
    }

    private fun setupClickListener() {
        binding.btnOpenCamera.setDebounceClickListener {
            analyticsHandler.postEvent(
                LendingKycEventKey.Clicked_Button_OCROptionScreen,
                mapOf(
                    LendingKycEventKey.scenario to PAN_OCR_FLOW,
                    LendingKycEventKey.optionChosen to binding.btnOpenCamera.getText()
                )
            )
            openImagePickerModule()
        }
        binding.toolbar.btnBack.setDebounceClickListener {
            analyticsHandler.postEvent(
                LendingKycEventKey.Clicked_Button_OCROptionScreen,
                mapOf(
                    LendingKycEventKey.scenario to PAN_OCR_FLOW,
                    LendingKycEventKey.optionChosen to BACK_ARROW
                )
            )
            popBackStack()
        }
    }

    private fun openImagePickerModule(){
        imagePickerManager.openImagePicker(
            ImagePickerOption(docType = DocType.PAN.name),
            findNavController(),
            PAN_OCR_FLOW
        ) {
            viewModel.postDocumentOcrRequest(DocType.PAN.name, it)
            popBackStack(R.id.panCapturePhotoFragment, false)
        }
    }

    private fun observeLiveData() {
        viewModel.documentOcrRequestLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                navigateTo(
                    FeatureLendingKycStepsNavigationDirections.actionToGenericLoadingDialog(
                        GenericLoadingArguments(
                            title = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_uploading_photo),
                            description = null,
                            assetsUrl = BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_LOADING,
                            illustrationResourceId = null,
                            isIllustrationUrl = false
                        )
                    ),
                    true
                )
            },
            onSuccess = {
                it?.let {
                    val name = it.name!!.split(" ")
                    val firstName = if (name.isNotEmpty()) name[0] else ""
                    val lastName = if (name.size > 1) name.filterIndexed { index, s -> index != 0 }
                        .joinToString(" ") else ""
                    creditReportPAN =
                        CreditReportPAN(it.getDocNumber(), firstName, lastName, it.dob.orEmpty())
                    loadingViewModel.updateGenericLoadingTitle(getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_fetching_pan_details))
                    loadingViewModel.dismissGenericLoadingAfterMillis(2000L, true, FROM_SCREEN)
                }
            },
            onErrorMessageCodeAndData = { message, errorCode, data ->
                when (errorCode) {
                    BaseConstants.ErrorCodesLendingKyc.PAN.INVALID_PAN_CARD -> {
                        navigateTo(
                            FeatureLendingKycStepsNavigationDirections.actionToPanErrorStatesFragment(
                                PanErrorStatesArguments(
                                    getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_no_pan_card_detected),
                                    getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_make_sure_the_photo_is_clear_and_visible),
                                    BaseConstants.CDN_BASE_URL + LendingKycConstants.IllustrationUrls.PAN_CARD_NOT_DETECTED_URL,
                                    PanErrorScreenPrimaryButtonAction.RETAKE_PHOTO,
                                    PanErrorScreenSecondaryButtonAction.NONE,
                                    isLottie = false
                                )
                            )
                        )
                    }
                    BaseConstants.ErrorCodesLendingKyc.PAN.UNABLE_TO_EXTRACT_DATA_FROM_FILE -> {
                        navigateTo(
                            FeatureLendingKycStepsNavigationDirections.actionToPanErrorStatesFragment(
                                PanErrorStatesArguments(
                                    getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_no_pan_card_detected),
                                    getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_make_sure_the_photo_is_clear_and_visible),
                                    BaseConstants.CDN_BASE_URL + LendingKycConstants.IllustrationUrls.PAN_CARD_NOT_DETECTED_URL,
                                    PanErrorScreenPrimaryButtonAction.RETAKE_PHOTO,
                                    PanErrorScreenSecondaryButtonAction.NONE,
                                    isLottie = false
                                )
                            )
                        )
                    }
                }
            }
        )
        loadingViewModel.genericLoadingOnAutoDismissViewModel.observe(viewLifecycleOwner) {
            if (it.isDismissingAfterSuccess && it.fromScreen == FROM_SCREEN) {
                // loading dialog dismissed go to next
                creditReportPAN?.let {
                    val args = encodeUrl(serializer.encodeToString(
                        CreditReportScreenArguments(
                        it,
                        false,
                        LendingKycConstants.PanFlowType.IMAGE,
                        isBackNavOrViewOnlyFlow = false,
                        PanErrorScreenPrimaryButtonAction.YES_DETAILS_ARE_CORRECT,
                        PanErrorScreenSecondaryButtonAction.NO_ENTER_DETAILS_MANUALLY,
                        fromScreen = FETCH_PAN_DETAILS_FROM_OCR_SCREEN,
                        description = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_please_confirm_if_these_are_your_pan_details)
                    )
                    ))
                    navigateTo(
                        FeatureLendingKycStepsNavigationDirections.actionToCreditReportFetchedStep(args)
                    )
                }
            }
        }
    }
}
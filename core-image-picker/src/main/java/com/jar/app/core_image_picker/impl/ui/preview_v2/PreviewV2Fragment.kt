package com.jar.app.core_image_picker.impl.ui.preview_v2

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.shape.CornerFamily
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.dp
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.data.dto.isFromLending
import com.jar.app.core_base.data.dto.isFromP2POrLending
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_image_picker.R
import com.jar.app.core_image_picker.databinding.CoreImagePickerFragmentPreviewV2Binding
import com.jar.app.core_image_picker.impl.ui.preview.PreviewFragment
import com.jar.app.core_image_picker.impl.util.CameraEventKey
import com.jar.app.core_image_picker.impl.util.ImagePickerConstants
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
internal class PreviewV2Fragment : BaseFragment<CoreImagePickerFragmentPreviewV2Binding>() {
    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var fromScreen = ""
    private var photoPath = ""

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (args.previewArgs.kycFeatureFlowType.isFromLending()){
                    EventBus.getDefault()
                        .post(LendingBackPressEvent(CameraEventKey.AADHAR_OCR_PREVIEW_SCREEN, true))
                }else{
                    popBackStack()
                }
            }
        }

    private val args by navArgs<PreviewV2FragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CoreImagePickerFragmentPreviewV2Binding
        get() = CoreImagePickerFragmentPreviewV2Binding::inflate

    companion object {
        const val SHOWN_PHOTO_PREVIEW_SCREEN = "Shown_PhotoPreviewScreen"
        const val TAKE_PHOTO_SCREEN = "Take Photo Screen"
        const val TAKE_PHOTO_CROP_SCREEN = "Take Photo Crop Screen"
        const val GALLERY_PHOTO_SELECT_SCREEN = "Gallery Photo Select Screen"
        const val GALLERY_PHOTO_CROP_ROTATE_SCREEN = "Gallery Photo Crop Rotate Screen"
        const val OCR_CAMERA_PREVIEW_SCREEN = "OCR_CAMERA_PREVIEW_SCREEN"
    }

    override fun setupAppBar() {
        updateToolbarTitle()
    }

    private fun updateToolbarTitle() {
        val toolbar = if (args.previewArgs.kycFeatureFlowType.isFromLending()) {
            ToolbarNone
        } else {
            ToolbarDefault(
                title = getString(R.string.core_image_picker_preview),
                showBackButton = true,
                showSeparator = true
            )
        }
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(toolbar)
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUi()
        setupListener()
        registerBackPressDispatcher()
    }

    private fun setupUi() {
        binding.clSelfiePreviewHolder.isVisible = args.previewArgs.isSelfie
        binding.previewSelfieOverlay.isVisible = if (args.previewArgs.kycFeatureFlowType.isFromP2POrLending()) false else args.previewArgs.isSelfie
        binding.selfiePreviewImage.isVisible = args.previewArgs.isSelfie
        binding.previewImage.isInvisible = args.previewArgs.isSelfie
        binding.btnCropRotate.isGone = args.previewArgs.isSelfie
        photoPath = args.previewArgs.previewPath
        fromScreen =
            if (args.previewArgs.isSelfie) PreviewFragment.TAKE_SELFIE else PreviewFragment.TAKE_PHOTO
        binding.previewImage.shapeAppearanceModel = binding.previewImage.shapeAppearanceModel
            .toBuilder()
            .setAllCorners(
                CornerFamily.ROUNDED,
                if (args.previewArgs.isSelfie) 32.dp.toFloat() else 8.dp.toFloat()
            )
            .build()
        inflatePreviewPhoto()
        analyticsHandler.postEvent(
            ImagePickerConstants.AnalyticsKeys.SHOWN_UPLOAD_PHOTO_SCREEN,
            mapOf(
                EventKey.FromScreen to fromScreen,
                EventKey.Lending.isFromLending to args.previewArgs.kycFeatureFlowType.isFromLending()
            )
        )
        if (args.previewArgs.isGalleryFlow) {
            binding.btnCropRotate.text = getString(R.string.core_image_picker_crop_and_rotate)
            binding.btnCropRotate.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.core_image_picker_ic_crop_rotate, 0, 0, 0
            )
        } else {
            binding.btnCropRotate.text = getString(R.string.core_image_picker_crop_image)
            binding.btnCropRotate.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.core_image_picker_ic_crop, 0, 0, 0
            )

        }
        if (args.previewArgs.isSelfie) {
            binding.tvEasyToRead.text = getString(
                R.string.core_image_picker_preview_your_selfie
            )
            binding.tvClearAndVisibleMessage.text = getString(
                R.string.core_image_picker_please_check_selfie_is_clear
            )
        } else {
            binding.tvEasyToRead.text = getString(
                R.string.core_image_picker_document_easy_to_read
            )
            binding.tvClearAndVisibleMessage.text = getString(
                R.string.core_image_picker_please_make_sure_document_is_clear_and_visible
            )
        }
        val cancelButtonText =
            if (args.previewArgs.isSelfie) getString(R.string.core_image_picker_retake_selfie)
            else if (args.previewArgs.isGalleryFlow) getString(R.string.core_image_picker_choose_a_different_photo)
            else getString(R.string.core_image_picker_retake_photo)
        binding.btnCancel.text = cancelButtonText
        binding.btnCancel.paintFlags = binding.btnCancel.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        analyticsHandler.postEvent(
            SHOWN_PHOTO_PREVIEW_SCREEN,
            mapOf(
                EventKey.SCENARIO to args.previewArgs.fromScreen,
                EventKey.FromScreen to if (args.previewArgs.isGalleryFlow) GALLERY_PHOTO_SELECT_SCREEN else TAKE_PHOTO_SCREEN
            )
        )
        if (args.previewArgs.kycFeatureFlowType.isFromLending()){
            binding.selfiePreviewImage.shapeAppearanceModel = binding.previewImage.shapeAppearanceModel
                .toBuilder()
                .setAllCorners(
                    CornerFamily.ROUNDED,
                    48.dp.toFloat()
                )
                .build()
            if (args.previewArgs.isSelfie)
                analyticsHandler.postEvent(
                    CameraEventKey.Lending_SelfieScreenClicked,
                    mapOf(CameraEventKey.action to CameraEventKey.preview_screen_shown)
                )
        }
    }

    private fun inflatePreviewPhoto() {
        Glide.with(requireContext())
            .load(File(photoPath))
            .into(
                if (args.previewArgs.isSelfie) binding.selfiePreviewImage
                else binding.previewImage
            )
    }

    private fun setupListener() {
        binding.btnConfirm.setDebounceClickListener {
            analyticsHandler.postEvent(
                ImagePickerConstants.AnalyticsKeys.CLICKED_USE_PHOTO_BUTTON_UPLOAD_PHOTO_SCREEN,
                mapOf(
                    EventKey.FromScreen to fromScreen,
                    EventKey.Lending.isFromLending to args.previewArgs.kycFeatureFlowType.isFromLending()
                )
            )
            if (args.previewArgs.kycFeatureFlowType.isFromLending() && args.previewArgs.isSelfie){
                analyticsHandler.postEvent(
                    CameraEventKey.Lending_SelfieScreenClicked,
                    mapOf(CameraEventKey.action to CameraEventKey.preview_screen_confirm_clicked)
                )
            }
            findNavController().previousBackStackEntry?.savedStateHandle
                ?.set(BaseConstants.SELECTED_PATH, photoPath)
        }
        binding.btnCancel.setDebounceClickListener {
            analyticsHandler.postEvent(
                ImagePickerConstants.AnalyticsKeys.CLICKED_RETAKE_BUTTON_UPLOAD_PHOTO_SCREEN,
                mapOf(
                    EventKey.FromScreen to fromScreen,
                    EventKey.Lending.isFromLending to args.previewArgs.kycFeatureFlowType.isFromLending()
                )
            )
            popBackStack()
        }
        binding.btnCropRotate.setDebounceClickListener {
            analyticsHandler.postEvent(
                OCR_CAMERA_PREVIEW_SCREEN,
                mapOf(
                    ImagePickerConstants.AnalyticsKeys.OPTION_CHOSEN to binding.btnCropRotate.text.toString(),
                    EventKey.Lending.isFromLending to args.previewArgs.kycFeatureFlowType.isFromLending()
                )
            )
            findNavController().currentBackStackEntry?.savedStateHandle
                ?.getLiveData<String>(BaseConstants.CROPPED_PATH)
                ?.observe(viewLifecycleOwner) {
                    photoPath = it
                    inflatePreviewPhoto()
                    analyticsHandler.postEvent(
                        SHOWN_PHOTO_PREVIEW_SCREEN,
                        mapOf(
                            EventKey.SCENARIO to args.previewArgs.fromScreen,
                            EventKey.FromScreen to
                                    if (args.previewArgs.isGalleryFlow) GALLERY_PHOTO_CROP_ROTATE_SCREEN
                                    else TAKE_PHOTO_CROP_SCREEN,
                            EventKey.Lending.isFromLending to args.previewArgs.kycFeatureFlowType.isFromLending()
                        )
                    )
                }
            navigateTo(
                PreviewV2FragmentDirections.actionPreviewV2FragmentToCropV2Fragment(
                    photoPath,
                    args.previewArgs.isGalleryFlow,
                    args.previewArgs.kycFeatureFlowType.name
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        updateToolbarTitle()
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
package com.jar.app.core_image_picker.impl.ui.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.shape.CornerFamily
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.BitmapUtils
import com.jar.app.base.util.dp
import com.jar.app.core_analytics.EventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_image_picker.R
import com.jar.app.core_image_picker.databinding.CoreImagePickerFragmentPreviewBinding
import com.jar.app.core_image_picker.impl.util.ImagePickerConstants
import com.jar.app.core_ui.extension.setDebounceClickListener
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
internal class PreviewFragment : BaseFragment<CoreImagePickerFragmentPreviewBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CoreImagePickerFragmentPreviewBinding
        get() = CoreImagePickerFragmentPreviewBinding::inflate

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var bitmapUtil: BitmapUtils

    private var fromScreen = ""

    private val args by navArgs<PreviewFragmentArgs>()

    companion object {
        const val TAKE_PHOTO = "Take photo"
        const val TAKE_SELFIE = "Take Selfie"
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarDefault(
                        title = getString(R.string.core_image_picker_upload_photo),
                        showBackButton = true,
                        showSeparator = true
                    )
                )
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUi()
        setupListener()
    }

    private fun setupUi() {
        binding.previewSelfieOverlay.isVisible = args.isSelfie

        fromScreen = if (args.isSelfie) TAKE_SELFIE else TAKE_PHOTO
        binding.previewImage.shapeAppearanceModel = binding.previewImage.shapeAppearanceModel
            .toBuilder()
            .setAllCorners(
                CornerFamily.ROUNDED,
                if (args.isSelfie) 32.dp.toFloat() else 8.dp.toFloat()
            )
            .build()
        Glide.with(requireContext())
            .load(File(args.previewPath))
            .into(binding.previewImage)
        analyticsHandler.postEvent(
            ImagePickerConstants.AnalyticsKeys.SHOWN_UPLOAD_PHOTO_SCREEN,
            mapOf(EventKey.FromScreen to fromScreen)
        )
    }

    private fun setupListener() {
        binding.useButton.setDebounceClickListener {
            analyticsHandler.postEvent(
                ImagePickerConstants.AnalyticsKeys.CLICKED_USE_PHOTO_BUTTON_UPLOAD_PHOTO_SCREEN,
                mapOf(EventKey.FromScreen to fromScreen)
            )
            findNavController().previousBackStackEntry?.savedStateHandle
                ?.set(BaseConstants.SELECTED_PATH, args.previewPath)
        }
        binding.retakeButton.setDebounceClickListener {
            analyticsHandler.postEvent(
                ImagePickerConstants.AnalyticsKeys.CLICKED_RETAKE_BUTTON_UPLOAD_PHOTO_SCREEN,
                mapOf(EventKey.FromScreen to fromScreen)
            )
            popBackStack()
        }
    }

}
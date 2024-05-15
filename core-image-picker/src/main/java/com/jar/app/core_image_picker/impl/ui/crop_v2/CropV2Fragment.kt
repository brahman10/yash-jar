package com.jar.app.core_image_picker.impl.ui.crop_v2

import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.data.dto.getKycFeatureFlowType
import com.jar.app.core_base.data.dto.isFromLending
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_image_picker.R
import com.jar.app.core_image_picker.databinding.CoreImagePickerFragmentCropV2Binding
import com.jar.app.core_image_picker.impl.ui.crop.CropViewModel
import com.jar.app.core_image_picker.impl.ui.crop.custom.CropView
import com.jar.app.core_image_picker.impl.util.CameraEventKey
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class CropV2Fragment : BaseFragment<CoreImagePickerFragmentCropV2Binding>() {

    private val viewModel by viewModels<CropViewModel>()
    private val args by navArgs<CropV2FragmentArgs>()

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private lateinit var cropView: CropView

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                EventBus.getDefault()
                    .post(
                        LendingBackPressEvent(
                            CameraEventKey.AADHAR_OCR_CROP_SCREEN,
                            shouldNavigateBack = true
                        )
                    )
            }
        }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CoreImagePickerFragmentCropV2Binding
        get() = CoreImagePickerFragmentCropV2Binding::inflate

    override fun setupAppBar() {
        val toolbar = if (getKycFeatureFlowType(args.kycFeatureFlowType).isFromLending()) {
            ToolbarNone
        } else {
            ToolbarDefault(
                title = if (args.isGalleryFlow)
                    getString(R.string.core_image_picker_crop_and_rotate)
                else getString(R.string.core_image_picker_crop_image),
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
        observeLiveData()
        registerBackPressDispatcher()
    }

    private fun observeLiveData() {
        viewModel.bitmapLiveData.observe(viewLifecycleOwner) { bitmap ->
            dismissProgressBar()
            bitmap?.let {
                cropView.setBitmap(
                    it,
                    0,
                    true,
                    true
                )
                cropView.setFreeform(true)
                binding.buttonHolder.isVisible = true
                binding.rotateButton.isVisible = args.isGalleryFlow
                Handler(Looper.getMainLooper()).postDelayed({
                    cropView.setInitialAspectRatio(true)
                }, 500L)
            }
        }
        viewModel.croppedPathLiveData.observe(viewLifecycleOwner) {
            dismissProgressBar()
            findNavController().previousBackStackEntry
                ?.savedStateHandle
                ?.set(BaseConstants.CROPPED_PATH, it)
            popBackStack()
        }
    }

    private fun setupListener() {
        binding.btnConfirm.setDebounceClickListener {
            showProgressBar()
            lifecycleScope.launch {
                if (::cropView.isInitialized)
                    viewModel.saveCroppedImageToFile(
                        cropView.result,
                        requireContext().getExternalFilesDir(BaseConstants.IMAGES_DIR)?.path!!
                    )
            }
        }
        binding.btnCancel.setDebounceClickListener {
            popBackStack()
        }
        binding.rotateButton.setDebounceClickListener {
            if (::cropView.isInitialized)
                cropView.rotate90Degrees()
        }
    }

    private fun setupUi() {
        cropView = CropView(requireContext())
        binding.cropContainer.removeAllViews()
        binding.cropContainer.addView(cropView)
        binding.btnCancel.paintFlags = binding.btnCancel.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        viewModel.getBitmapFromPath(args.photoPath)
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
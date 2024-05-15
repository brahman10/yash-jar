package com.jar.app.core_image_picker.impl.ui.crop

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.isAndroidSDK13
import com.jar.app.base.util.isAndroidSDK13OrElse
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_image_picker.R
import com.jar.app.core_image_picker.databinding.CoreImagePickerFragmentCropBinding
import com.jar.app.core_image_picker.impl.ui.crop.custom.CropView
import com.jar.app.core_image_picker.impl.util.ImagePickerConstants
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class CropFragment : BaseFragment<CoreImagePickerFragmentCropBinding>() {

    private val outputDir by lazy { requireContext().getExternalFilesDir(BaseConstants.IMAGES_DIR) }
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CoreImagePickerFragmentCropBinding
        get() = CoreImagePickerFragmentCropBinding::inflate

    private val viewModel by viewModels<CropViewModel>()

    @Inject
    lateinit var analyticsHandler: AnalyticsApi
    private lateinit var selectedPath: String
    private lateinit var cropView: CropView
    private val galleryPickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let {
                showProgressBar()
                viewModel.getPathFromUri(it, outputDir?.absolutePath.orEmpty())
            } ?: run {
                popBackStack()
            }
        }

    companion object {
        const val GALLERY_SECTION = "gallery section"
        private const val REQUIRED_PERMISSIONS_FOR_GALLERY =
            Manifest.permission.READ_EXTERNAL_STORAGE

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private const val REQUIRED_PERMISSIONS_FOR_GALLERY_FOR_SDK_33 =
            Manifest.permission.READ_MEDIA_IMAGES
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
        observeLiveData()
        openOrAskGalleryPermission {
            openGallery()
        }
    }

    private val permissionLauncherForGallery = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openGallery()
        } else {
            if (shouldShowRequestPermissionRationale(
                    if (isAndroidSDK13()) REQUIRED_PERMISSIONS_FOR_GALLERY_FOR_SDK_33 else REQUIRED_PERMISSIONS_FOR_GALLERY
                )
            ) {
                getString(R.string.core_image_picker_provide_gallery_permissions).snackBar(
                    binding.root
                )
            } else {
                openPermissionSettings(getString(R.string.core_image_picker_provide_gallery_permissions))
            }
        }
    }

    private fun openPermissionSettings(message: String) {
        try {
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    data = Uri.fromParts("package", requireActivity().packageName, null)
                }
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun openOrAskGalleryPermission(action: () -> Unit) {
        if (isGalleryPermissionsGranted()) {
            action.invoke()
        } else {
            isAndroidSDK13OrElse({
                permissionLauncherForGallery.launch(REQUIRED_PERMISSIONS_FOR_GALLERY_FOR_SDK_33)
            }) {
                permissionLauncherForGallery.launch(REQUIRED_PERMISSIONS_FOR_GALLERY)
            }
        }
    }

    private fun isGalleryPermissionsGranted() = if (isAndroidSDK13()) {
        ContextCompat.checkSelfPermission(
            requireContext(), REQUIRED_PERMISSIONS_FOR_GALLERY_FOR_SDK_33
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        ContextCompat.checkSelfPermission(
            requireContext(), REQUIRED_PERMISSIONS_FOR_GALLERY
        ) == PackageManager.PERMISSION_GRANTED
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
                binding.rotateButton.isVisible = true
                Handler(Looper.getMainLooper()).postDelayed({
                    cropView.setInitialAspectRatio(true)
                }, 500L)
            }
        }
        viewModel.pathLiveData.observe(viewLifecycleOwner) {
            it?.let {
                selectedPath = it
                viewModel.getBitmapFromPath(it)
            } ?: run {
                dismissProgressBar()
            }
        }
        viewModel.croppedPathLiveData.observe(viewLifecycleOwner) {
            dismissProgressBar()
            findNavController().currentBackStackEntry
                ?.savedStateHandle
                ?.set(BaseConstants.SELECTED_PATH, it)
        }
    }

    private fun setupListener() {
        binding.usePhotoButton.setDebounceClickListener {
            analyticsHandler.postEvent(
                ImagePickerConstants.AnalyticsKeys.CLICKED_USE_PHOTO_BUTTON_UPLOAD_PHOTO_SCREEN,
                mapOf(EventKey.FromScreen to GALLERY_SECTION)
            )
            showProgressBar()
            lifecycleScope.launch {
                viewModel.saveCroppedImageToFile(
                    cropView.result,
                    requireContext().getExternalFilesDir(BaseConstants.IMAGES_DIR)?.path!!
                )
            }
        }
        binding.chooseDifferentButton.setDebounceClickListener {
            analyticsHandler.postEvent(
                ImagePickerConstants.AnalyticsKeys.CLICKED_CHOOSE_DIFFERENT_BUTTON_UPLOAD_PHOTO_SCREEN,
                mapOf(EventKey.FromScreen to GALLERY_SECTION)
            )
            openOrAskGalleryPermission {
                openGallery()
            }
        }
        binding.rotateButton.setDebounceClickListener {
            if (::cropView.isInitialized)
                cropView.rotate90Degrees()
        }
    }

    private fun openGallery() {
        galleryPickerLauncher.launch("image/*")
    }

    private fun setupUi() {
        cropView = CropView(requireContext())
        binding.cropContainer.removeAllViews()
        binding.cropContainer.addView(cropView)

    }
}
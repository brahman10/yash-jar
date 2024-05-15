package com.jar.app.core_image_picker.impl.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Rational
import android.util.Size
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withStateAtLeast
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.LendingToolbarVisibilityEventV2
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.isAndroidSDK13
import com.jar.app.base.util.isAndroidSDK13OrElse
import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.core_base.data.dto.isFromLending
import com.jar.app.core_base.data.dto.isFromP2PInvestment
import com.jar.app.core_image_picker.R
import com.jar.app.core_image_picker.api.data.CameraType
import com.jar.app.core_image_picker.databinding.CoreImagePickerFragmentCameraBinding
import com.jar.app.core_image_picker.impl.data.CameraArguments
import com.jar.app.core_image_picker.impl.data.DocumentType
import com.jar.app.core_image_picker.impl.data.PreviewV2Arguments
import com.jar.app.core_image_picker.impl.util.ImagePickerConstants
import com.jar.app.core_image_picker.impl.util.CameraEventKey
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
internal class CameraFragment : BaseFragment<CoreImagePickerFragmentCameraBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CoreImagePickerFragmentCameraBinding
        get() = CoreImagePickerFragmentCameraBinding::inflate

    private val viewModel: CameraViewModel by viewModels()
    private var callbackExecutor = lazy { Executors.newSingleThreadExecutor() }
    private val outputDir by lazy { requireContext().getExternalFilesDir(BaseConstants.IMAGES_DIR) }
    private val args by navArgs<CameraFragmentArgs>()

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    private var cameraProvider: ProcessCameraProvider? = null

    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private var imageCapture: ImageCapture? = null
    private var isCameraStarted: Boolean = false

    private lateinit var cameraExecutor: ExecutorService
    private var flashMode: Int = ImageCapture.FLASH_MODE_OFF
    private var cameraType: CameraType = CameraType.DOC_SINGLE_SIDE
    private var lastBrightness = 0f
    private var width = 0
    private var height = 0
    private val galleryPickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let {
                showProgressBar()
                viewModel.getPathFromUri(it, outputDir?.absolutePath.orEmpty())
            } ?: run {
                popBackStack()
            }
        }

    private val cameraArgs by lazy {
        serializer.decodeFromString<CameraArguments>(decodeUrl(args.cameraArgs))
    }

    companion object {
        private const val ENABLE = "Enable"
        private const val DISABLE = "Disable"
        private const val REQUIRED_PERMISSIONS_FOR_CAMERA = Manifest.permission.CAMERA
        private const val REQUIRED_PERMISSIONS_FOR_GALLERY =
            Manifest.permission.READ_EXTERNAL_STORAGE

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private const val REQUIRED_PERMISSIONS_FOR_GALLERY_FOR_SDK_33 =
            Manifest.permission.READ_MEDIA_IMAGES
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            binding.clNoPermission.isVisible = false
            startCamera()
        } else {
            if (shouldShowRequestPermissionRationale(REQUIRED_PERMISSIONS_FOR_CAMERA)) {
                binding.clNoPermission.isVisible = true
                getString(R.string.core_image_picker_provide_camera_permissions).snackBar(
                    binding.root
                )
            } else {
                openPermissionSettings(getString(R.string.core_image_picker_provide_camera_permissions))
            }
        }
    }

    private val permissionLauncherForGallery = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            galleryPickerLauncher.launch("image/*")
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

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        cameraType = cameraArgs.cameraType
        lastBrightness = requireActivity().window.attributes?.screenBrightness ?: 0f
        setupExecutor()
        setupCameraBasedOnType()
        setupUi()
        setClickListener()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.captureLiveData.observe(viewLifecycleOwner) {

            if (isP2POrLendingFlow()) {
                navigateTo(
                    CameraFragmentDirections.actionCameraFragmentToPreviewV2Fragment(
                        PreviewV2Arguments(
                            it,
                            isSelfieCamera(),
                            false,
                            cameraArgs.fromScreen,
                            cameraArgs.kycFeatureFlowType
                        )
                    )
                )
            } else {
                navigateTo(
                    CameraFragmentDirections.actionCameraFragmentToPreviewFragment(
                        it,
                        isSelfieCamera()
                    )
                )
            }
        }

        viewModel.gallerySelectedPathLiveData.observe(viewLifecycleOwner) {
            dismissProgressBar()
            it?.let {
                navigateTo(
                    CameraFragmentDirections.actionCameraFragmentToPreviewV2Fragment(
                        PreviewV2Arguments(
                            it,
                            isSelfieCamera(),
                            true,
                            cameraArgs.fromScreen,
                            cameraArgs.kycFeatureFlowType
                        )
                    )
                )
            } ?: run {
                getString(R.string.core_image_picker_something_went_wrong).snackBar(binding.root)
            }
        }
    }

    private fun isP2POrLendingFlow() =
        isLendingKycFlow() || cameraArgs.kycFeatureFlowType.isFromP2PInvestment()

    private fun isSelfieCamera() = cameraType == CameraType.SELFIE
    private fun setupCameraBasedOnType() {
        when (cameraType) {
            CameraType.DOC_SINGLE_SIDE,
            CameraType.DOC_TWO_SIDE -> {
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                binding.selfieOverlayView.isGone = true
                binding.flashModeButton.isGone = false
                binding.documentOverlayView.isGone = false
                binding.messageText.isGone = false
                binding.descriptionText.isGone = false
                binding.selfieMessageText.isGone = true
                binding.docFrontSideText.isGone = false
                binding.galleryButton.isVisible = isP2POrLendingFlow()
                binding.tvPoweredBy.isVisible = false
                binding.tvToolbarTitle.text = getString(R.string.core_image_picker_take_photo)
            }
            CameraType.SELFIE -> {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                binding.flashModeButton.isGone = true
                binding.selfieOverlayView.isGone = false
                binding.documentOverlayView.isGone = true
                binding.messageText.isGone = true
                binding.descriptionText.isGone = true
                binding.selfieMessageText.isVisible = true
                binding.docFrontSideText.isVisible = false
                binding.galleryButton.isVisible = false
                binding.tvPoweredBy.isVisible = true
                binding.tvToolbarTitle.text = getString(R.string.core_image_picker_take_selfie)
                setBrightnessTo(1.0f)
            }
        }
        binding.documentOverlayView.setVerticalDocument(
            cameraType == CameraType.DOC_TWO_SIDE
        )
    }

    private fun isLendingKycFlow() =
        cameraArgs.kycFeatureFlowType.isFromLending() || cameraArgs.fromScreen.isNotEmpty()

    private fun getLabelMessageByDocumentType(documentType: DocumentType): String {
        val documentName = when (documentType) {
            DocumentType.PAN -> getString(R.string.core_image_picker_pan_card)
            DocumentType.AADHAAR -> getString(R.string.core_image_picker_aadhar_card)
            DocumentType.LICENSE -> getString(R.string.core_image_picker_driving_license)
            DocumentType.VOTER_ID -> getString(R.string.core_image_picker_voters_id)
            DocumentType.PASSPORT -> getString(R.string.core_image_picker_passport)
            DocumentType.DEFAULT -> getString(R.string.core_image_picker_document)
        }
        return getString(
            R.string.core_image_picker_place_your_document_inside_the_frame,
            documentName
        )
    }

    private fun setBrightnessTo(value: Float) {
        val params = requireActivity().window.attributes?.apply {
            screenBrightness = value
        }
        requireActivity().window.apply {
            attributes = params
            addFlags(WindowManager.LayoutParams.FLAGS_CHANGED)
        }
    }

    private fun setupExecutor() {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun setupUi() {
        analyticsHandler.postEvent(
            ImagePickerConstants.AnalyticsKeys.OCR_CAMERA_SCREEN,
            mapOf(
                ImagePickerConstants.AnalyticsKeys.OPTION_CHOSEN to ImagePickerConstants.AnalyticsKeys.SHOWN,
                ImagePickerConstants.AnalyticsKeys.fromScreen to cameraArgs.fromScreen,
                ImagePickerConstants.AnalyticsKeys.isLendingFlow to isLendingKycFlow()
            )
        )
        toggleFlashOnUi()
        doOrAskCameraPermission {
            binding.clNoPermission.isVisible = false
            startCamera()
        }
        val message = getLabelMessageByDocumentType(DocumentType.valueOf(cameraArgs.docType))
        binding.messageText.text = message
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

    private fun doOrAskCameraPermission(action: () -> Unit) {
        if (isCameraPermissionsGranted()) {
            action.invoke()
        } else {
            analyticsHandler.postEvent(CameraEventKey.Lending_CameraPermissionScreenPopupShown)
            permissionLauncher.launch(REQUIRED_PERMISSIONS_FOR_CAMERA)
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun setClickListener() {
        binding.flashModeButton.setDebounceClickListener {
            flashMode = if (flashMode == ImageCapture.FLASH_MODE_ON) ImageCapture.FLASH_MODE_OFF
            else ImageCapture.FLASH_MODE_ON
            imageCapture?.flashMode = flashMode
            toggleFlashOnUi()
            analyticsHandler.postEvent(
                ImagePickerConstants.AnalyticsKeys.CLICKED_FLASH_BUTTON_TAKE_PHOTO_SCREEN,
                mapOf(
                    ImagePickerConstants.AnalyticsKeys.OPTION_CHOSEN
                            to if (flashMode == ImageCapture.FLASH_MODE_ON) ENABLE else DISABLE,
                    ImagePickerConstants.AnalyticsKeys.fromScreen to cameraArgs.fromScreen,
                    ImagePickerConstants.AnalyticsKeys.isLendingFlow to isLendingKycFlow()
                )
            )
        }
        binding.captureButton.setDebounceClickListener {
            doOrAskCameraPermission {
                imageCapture?.let {
                    if (isLendingKycFlow()) {
                        analyticsHandler.postEvent(
                            ImagePickerConstants.AnalyticsKeys.OCR_CAMERA_SCREEN,
                            mapOf(
                                ImagePickerConstants.AnalyticsKeys.fromScreen to cameraArgs.fromScreen,
                                ImagePickerConstants.AnalyticsKeys.OPTION_CHOSEN to ImagePickerConstants.AnalyticsKeys.CLICKED_CAPTURE_BUTTON,
                                ImagePickerConstants.AnalyticsKeys.isLendingFlow to isLendingKycFlow()
                            )
                        )
                    }else{
                        val eventName = if (isSelfieCamera())
                            ImagePickerConstants.AnalyticsKeys.CLICKED_SELFIE_CAPTURE_BUTTON_TAKE_SELFIE_SCREEN
                        else
                            ImagePickerConstants.AnalyticsKeys.CLICKED_PHOTO_CAPTURE_BUTTON_TAKE_PHOTO_SCREEN_SCREEN
                        analyticsHandler.postEvent(eventName)
                    }
                    uiScope.launch {
                        binding.selfieOverlayView.shouldShowWhiteOverlay(true)
                        delay(500L)
                        viewModel.capturePhoto(
                            it,
                            outputDir?.absolutePath.orEmpty(),
                            callbackExecutor.value,
                            cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA
                        )
                        binding.selfieOverlayView.shouldShowWhiteOverlay(false)
                    }
                }
            }
        }
        binding.btnBack.setDebounceClickListener {
            if (isSelfieCamera())
                analyticsHandler.postEvent(
                    ImagePickerConstants.AnalyticsKeys.CLICKED_BACK_ARROW_BUTTON_TAKE_SELFIE_SCREEN,
                    mapOf(
                        ImagePickerConstants.AnalyticsKeys.fromScreen to cameraArgs.fromScreen,
                        ImagePickerConstants.AnalyticsKeys.isLendingFlow to isLendingKycFlow()
                    )
                )
            analyticsHandler.postEvent(
                ImagePickerConstants.AnalyticsKeys.OCR_CAMERA_SCREEN,
                mapOf(
                    ImagePickerConstants.AnalyticsKeys.fromScreen to cameraArgs.fromScreen,
                    ImagePickerConstants.AnalyticsKeys.OPTION_CHOSEN to ImagePickerConstants.AnalyticsKeys.Camera_BackButtonClicked,
                    ImagePickerConstants.AnalyticsKeys.isLendingFlow to isLendingKycFlow()
                )
            )
            popBackStack()
        }
        binding.galleryButton.setDebounceClickListener {
            analyticsHandler.postEvent(
                ImagePickerConstants.AnalyticsKeys.OCR_CAMERA_SCREEN,
                mapOf(
                    ImagePickerConstants.AnalyticsKeys.fromScreen to cameraArgs.fromScreen,
                    ImagePickerConstants.AnalyticsKeys.OPTION_CHOSEN to ImagePickerConstants.AnalyticsKeys.CLICKED_GALLERY_BUTTON,
                    ImagePickerConstants.AnalyticsKeys.isLendingFlow to isLendingKycFlow()
                )
            )
            openOrAskGalleryPermission {
                galleryPickerLauncher.launch("image/*")
            }
        }

        binding.btnAllowPermission.setDebounceClickListener {
            doOrAskCameraPermission {
                binding.clNoPermission.isVisible = false
                startCamera()
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

    private fun toggleFlashOnUi() {
        when (flashMode) {
            ImageCapture.FLASH_MODE_OFF -> {
                binding.flashModeButton.setImageResource(R.drawable.ic_flash_mode_off)
            }
            ImageCapture.FLASH_MODE_ON -> {
                binding.flashModeButton.setImageResource(R.drawable.ic_flash_mode_on)
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            try {
                // Unbind use cases before rebinding
                cameraProvider?.unbindAll()
                val useCaseGroupBuilder = UseCaseGroup.Builder()
                if (isSelfieCamera()) {
                    val boxRect = binding.selfieOverlayView.getCropRect()!!
                    width = Resources.getSystem().displayMetrics.widthPixels
                    height = Resources.getSystem().displayMetrics.heightPixels
                    val imageAnalysis = createImageAnalysisUseCase()
                    imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(requireActivity()),
                        SelfieFaceAnalyzer(
                            boxRect,
                            binding.previewView
                        ) { isFaceDetected, message ->
                            uiScope.launch {
                                binding.selfieOverlayView.setValidSelfie(isFaceDetected)
                                binding.captureButton.setCaptureDisabled(!isFaceDetected)
                                binding.selfieMessageText.text = message.ifEmpty {
                                    getString(R.string.core_image_picker_make_sure_your_face_is_in_the_frame)
                                }
                                if (isFaceDetected && isP2POrLendingFlow()) {
                                    binding.selfieMessageText.text =
                                        getString(R.string.core_image_picker_awesome_click_the_selfie_now)
                                }
                            }
                        }
                    )
                    useCaseGroupBuilder.addUseCase(imageAnalysis)
                } else {
                    viewLifecycleOwner.lifecycleScope.launch {
                        withStateAtLeast(Lifecycle.State.STARTED){
                            binding.documentOverlayView.getCropBoxRect()?.let {
                                width = it.width().toInt()
                                height = it.height().toInt()
                            }?:run{
                                width = Resources.getSystem().displayMetrics.widthPixels - 32.dp //padding
                                height = 200.dp
                            }
                        }
                    }
                }
                val viewPort = ViewPort.Builder(
                    Rational(width, height),
                    binding.previewView.display.rotation
                ).build()
                val useCaseGroup = useCaseGroupBuilder
                    .addUseCase(createPreviewUseCase())
                    .addUseCase(createCaptureUseCase())
                    .setViewPort(viewPort)
                    .build()
                // Bind use cases to camera
                cameraProvider?.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    useCaseGroup
                )
                isCameraStarted = true
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(requireActivity()))
    }

    private fun createImageAnalysisUseCase(): ImageAnalysis {
        return ImageAnalysis.Builder()
            .setTargetRotation(binding.previewView.display.rotation)
            .setTargetResolution(Size(width, height))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    private fun createPreviewUseCase(): UseCase {
        val widthPixels = Resources.getSystem().displayMetrics.widthPixels
        val heightPixels = Resources.getSystem().displayMetrics.heightPixels
        return Preview.Builder()
            .setTargetRotation(binding.previewView.display.rotation)
            .setTargetResolution(Size(widthPixels, heightPixels))
            .build()
            .also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }
    }

    private fun createCaptureUseCase(): UseCase {
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(flashMode)
            .setTargetRotation(binding.previewView.display.rotation)
            .setTargetResolution(Size(width, height))
            .build()
        return imageCapture!!
    }

    private fun isCameraPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(), REQUIRED_PERMISSIONS_FOR_CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun isGalleryPermissionsGranted() = if (isAndroidSDK13()) {
        ContextCompat.checkSelfPermission(
            requireContext(), REQUIRED_PERMISSIONS_FOR_GALLERY_FOR_SDK_33
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        ContextCompat.checkSelfPermission(
            requireContext(), REQUIRED_PERMISSIONS_FOR_GALLERY
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        cameraProvider?.unbindAll()
        EventBus.getDefault().post(LendingToolbarVisibilityEventV2(false))
        setBrightnessTo(lastBrightness)
        cameraExecutor.shutdown()
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        if (isCameraPermissionsGranted()) {
            binding.clNoPermission.isVisible = false
            if (isCameraStarted.not()) {
                startCamera()
            }
        }
        EventBus.getDefault().post(LendingToolbarVisibilityEventV2(true))
    }
}
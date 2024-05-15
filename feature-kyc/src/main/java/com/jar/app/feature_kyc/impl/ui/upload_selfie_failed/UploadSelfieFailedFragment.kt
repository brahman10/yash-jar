package com.jar.app.feature_kyc.impl.ui.upload_selfie_failed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_image_picker.api.ImagePickerManager
import com.jar.app.core_image_picker.api.data.CameraType
import com.jar.app.core_image_picker.api.data.ImagePickerOption
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_kyc.KycV2NavigationDirections
import com.jar.app.feature_kyc.R
import com.jar.app.feature_kyc.databinding.FragmentUploadSelfieFailedBinding
import com.jar.app.feature_kyc.shared.domain.model.KycStatus
import com.jar.app.feature_kyc.shared.domain.model.KycVerificationStatus
import com.jar.app.feature_kyc.shared.util.KycConstants
import com.jar.app.feature_kyc.shared.util.KycConstants.AnalyticsKeys.ButtonClicked
import com.jar.app.feature_kyc.shared.util.KycConstants.AnalyticsKeys.PhotoVerificationError_Screen_Clicked
import com.jar.app.feature_kyc.shared.util.KycConstants.AnalyticsKeys.PhotoVerificationError_Screen_Shown
import com.jar.app.feature_kyc.shared.util.KycConstants.AnalyticsKeys.Retake
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
internal class UploadSelfieFailedFragment: BaseFragment<FragmentUploadSelfieFailedBinding>() {

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var imagePickerManager: ImagePickerManager

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args by navArgs<UploadSelfieFailedFragmentArgs>()

    private val viewModelProvider by viewModels<UploadSelfieFailedViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentUploadSelfieFailedBinding
        get() = FragmentUploadSelfieFailedBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarDefault(
                        getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_photo_verification),
                        showSeparator = true
                    )
                )
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        initClickListeners()
        observeLiveData()
        analyticsHandler.postEvent(PhotoVerificationError_Screen_Shown)
    }

    private fun setupUI() {
        binding.tvSelfieTitle.text = args.title
        binding.tvSelfieDescription.text = args.description
    }

    private fun initClickListeners() {
        binding.btnRetakeSelfie.setDebounceClickListener {
            analyticsHandler.postEvent(
                KycConstants.AnalyticsKeys.CLICKED_RETAKE_SELFIE_BUTTON_PHOTO_VERIFICATION_ISSUE_SCREEN
            )
            analyticsHandler.postEvent(PhotoVerificationError_Screen_Clicked, ButtonClicked, Retake)
            imagePickerManager.openImagePicker(
                ImagePickerOption(
                    cameraType = CameraType.SELFIE,
                    docType = args.docType.name
                )
            ) {
                viewModel.postFaceMatchRequest(args.docType.name, File(it).readBytes())
                popBackStack(R.id.uploadSelfieFailedFragment, false)
            }
        }
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.postFaceMatchRequestLiveData.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            val kycStatus = KycStatus(
                                title = it.title,
                                description = it.description,
                                shareMsg = it.shareMsg,
                                verificationStatus = it.verificationStatus
                            )
                            val encoded = encodeUrl(serializer.encodeToString(kycStatus))
                            when (it.verificationStatus) {
                                KycVerificationStatus.VERIFIED.name -> {
                                    navigateTo(
                                        "android-app://com.jar.app/kycVerificationStatusFragment/$encoded/${args.fromScreen}",
                                        popUpTo = R.id.uploadSelfieFailedFragment,
                                        inclusive = true
                                    )
                                }

                                KycVerificationStatus.FAILED.name -> {
                                    navigateTo(
                                        "android-app://com.jar.app/kycVerificationStatusFragment/$encoded/${args.fromScreen}",
                                        popUpTo = R.id.uploadSelfieFailedFragment,
                                        inclusive = true
                                    )
                                }

                                KycVerificationStatus.PENDING.name -> {
                                    navigateTo(
                                        "android-app://com.jar.app/kycVerificationStatusFragment/$encoded/${args.fromScreen}",
                                        popUpTo = R.id.uploadSelfieFailedFragment,
                                        inclusive = true
                                    )
                                }

                                KycVerificationStatus.RETRY.name -> {
                                    navigateTo(
                                        KycV2NavigationDirections.actionToUploadSelfieFailedFragment(
                                            docType = args.docType,
                                            title = it.title.orEmpty(),
                                            description = it.description.orEmpty(),
                                            fromScreen = args.fromScreen
                                        ),
                                        popUpTo = R.id.uploadSelfieFailedFragment,
                                        inclusive = true
                                    )
                                }
                            }
                            val errorMessage = kycStatus.description ?: ""
                            analyticsHandler.postEvent(
                                KycConstants.AnalyticsKeys.SHOWN_PHOTO_VERIFICATION_ISSUE_SCREEN,
                                mapOf(KycConstants.AnalyticsKeys.ERROR_MESSAGE to errorMessage)
                            )
                            analyticsHandler.postEvent(PhotoVerificationError_Screen_Shown)
                        }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        dismissProgressBar()
                    }
                )
            }
        }
    }
}
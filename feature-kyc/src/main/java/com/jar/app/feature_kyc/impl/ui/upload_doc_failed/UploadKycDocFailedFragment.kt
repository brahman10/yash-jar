package com.jar.app.feature_kyc.impl.ui.upload_doc_failed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.getLocalString
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_image_picker.api.ImagePickerManager
import com.jar.app.core_image_picker.api.data.CameraType
import com.jar.app.core_image_picker.api.data.ImagePickerOption
import com.jar.app.core_image_picker.api.data.ImageSelectionSource
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_kyc.KycV2NavigationDirections
import com.jar.app.feature_kyc.R
import com.jar.app.feature_kyc.databinding.FragmentUploadKycDocFailedBinding
import com.jar.app.feature_kyc.shared.domain.model.DocType
import com.jar.app.feature_kyc.shared.domain.model.KycOcrResponse
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_kyc.impl.ui.upload_options.UploadOptionsBottomSheetFragment
import com.jar.app.feature_kyc.shared.util.KycConstants
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class UploadKycDocFailedFragment : BaseFragment<FragmentUploadKycDocFailedBinding>() {

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var imagePickerManager: ImagePickerManager

    @Inject
    lateinit var prefsApi: PrefsApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args by navArgs<UploadKycDocFailedFragmentArgs>()

    private val viewModelProvider by viewModels<UploadKycDocFailedViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val kycOcrResponse by lazy {
        serializer.decodeFromString<KycOcrResponse>(decodeUrl(args.kycOcrResponse))
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentUploadKycDocFailedBinding
        get() = FragmentUploadKycDocFailedBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarDefault(
                        getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_upload_document),
                        showSeparator = true
                    )
                )
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        listenUploadOptionSection()
        setupUI()
        initClickListeners()
        observeLiveData()
    }

    private fun setupUI() {
        binding.tvKycTitle.text = kycOcrResponse.title
        binding.tvKycDescription.text = kycOcrResponse.description
        Glide.with(requireContext()).load(kycOcrResponse.errorImage).into(binding.ivKycDoc)
        val errorMessage = kycOcrResponse.title ?: ""
        analyticsHandler.postEvent(
            KycConstants.AnalyticsKeys.SHOWN_UPLOAD_DOCUMENT_ERROR_SCREEN,
            mapOf(
                KycConstants.AnalyticsKeys.ERROR_MESSAGE to errorMessage
            )
        )
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {

                viewModel.kycOcrRequestLiveData.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        if (!it?.errorImage.isNullOrBlank()) {
                            val encoded = encodeUrl(serializer.encodeToString(it))
                            navigateTo(
                                KycV2NavigationDirections.actionToUploadKycDocFailedFragment(
                                    encoded,
                                    args.fromScreen
                                ),
                                popUpTo = R.id.uploadKycDocFailedFragment,
                                inclusive = true
                            )
                        } else {
                            navigateTo(
                                KycV2NavigationDirections.actionToUploadSelfieFragment(
                                    kycOcrResponse.getDocType(),
                                    args.fromScreen
                                ),
                                popUpTo = R.id.uploadKycDocFailedFragment,
                                inclusive = true
                            )
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }
    }

    private fun initClickListeners() {
        val title: String = getCustomString(kycOcrResponse.getDocType().uploadTitle)
        binding.btnRetakePhoto.setDebounceClickListener {
            val errorMessage = kycOcrResponse.title ?: ""
            analyticsHandler.postEvent(
                KycConstants.AnalyticsKeys.CLICKED_RETAKE_PHOTO_UPLOAD_DOCUMENT_ERROR_SCREEN,
                mapOf(
                    KycConstants.AnalyticsKeys.ERROR_MESSAGE to errorMessage
                )
            )
            navigateTo(KycV2NavigationDirections.actionToUploadOptionsBottomSheetFragment(title))
        }
    }

    private fun getCameraTypeForDocument(docType: DocType): CameraType {
        return when (docType) {
            DocType.PAN -> CameraType.DOC_SINGLE_SIDE
            DocType.AADHAAR -> CameraType.DOC_SINGLE_SIDE
            DocType.LICENSE -> CameraType.DOC_SINGLE_SIDE
            DocType.VOTER_ID -> CameraType.DOC_TWO_SIDE
            DocType.PASSPORT -> CameraType.DOC_TWO_SIDE
            DocType.DEFAULT -> CameraType.DOC_SINGLE_SIDE
        }
    }

    private fun listenUploadOptionSection() {
        setFragmentResultListener(
            UploadOptionsBottomSheetFragment.UPLOAD_OPTION_SELECTION_REQUEST_KEY
        ) { _, bundle ->
            when (bundle.getString(UploadOptionsBottomSheetFragment.OPTION_SELECTION_TYPE)) {
                UploadOptionsBottomSheetFragment.OPTION_CAMERA -> {
                    analyticsHandler.postEvent(
                        KycConstants.AnalyticsKeys.CLICKED_BOTTOM_SHEET_BUTTON_UPLOAD_DOCUMENT_ERROR_SCREEN,
                        mapOf(
                            KycConstants.AnalyticsKeys.OPTION_CHOSEN
                                    to getCustomLocalizedString(
                                requireContext(),
                                com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_take_a_photo,
                                prefsApi.getCurrentLanguageCode()
                            )
                        )
                    )
                    imagePickerManager.openImagePicker(
                        ImagePickerOption(
                            docType = kycOcrResponse.docType,
                            cameraType = getCameraTypeForDocument(kycOcrResponse.getDocType())
                        )
                    ) {
                        viewModel.postKycOcrRequest(kycOcrResponse.docType, File(it).readBytes())
                        popBackStack(R.id.uploadKycDocFailedFragment, false)
                    }
                }

                UploadOptionsBottomSheetFragment.OPTION_GALLERY -> {
                    analyticsHandler.postEvent(
                        KycConstants.AnalyticsKeys.CLICKED_BOTTOM_SHEET_BUTTON_UPLOAD_DOCUMENT_ERROR_SCREEN,
                        mapOf(
                            KycConstants.AnalyticsKeys.OPTION_CHOSEN
                                    to getCustomLocalizedString(
                                requireContext(),
                                com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_choose_from_gallery,
                                prefsApi.getCurrentLanguageCode()
                            )
                        )
                    )
                    imagePickerManager.openImagePicker(
                        ImagePickerOption(
                            imageSelectionSource = ImageSelectionSource.GALLERY,
                            docType = kycOcrResponse.docType
                        )
                    ) {
                        viewModel.postKycOcrRequest(kycOcrResponse.docType, File(it).readBytes())
                        popBackStack(R.id.uploadKycDocFailedFragment, false)
                    }
                }
            }
        }
    }
}
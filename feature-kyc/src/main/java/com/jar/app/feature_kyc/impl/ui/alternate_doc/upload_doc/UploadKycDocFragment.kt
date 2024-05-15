package com.jar.app.feature_kyc.impl.ui.alternate_doc.upload_doc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_image_picker.api.ImagePickerManager
import com.jar.app.core_image_picker.api.data.CameraType
import com.jar.app.core_image_picker.api.data.ImagePickerOption
import com.jar.app.core_image_picker.api.data.ImageSelectionSource
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_kyc.KycV2NavigationDirections
import com.jar.app.feature_kyc.R
import com.jar.app.feature_kyc.databinding.FragmentUploadKycDocBinding
import com.jar.app.feature_kyc.shared.domain.model.DocType
import com.jar.app.feature_kyc.shared.util.KycConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
internal class UploadKycDocFragment : BaseFragment<FragmentUploadKycDocBinding>() {

    @Inject
    lateinit var serializer: Serializer

    private val args by navArgs<UploadKycDocFragmentArgs>()

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var prefsApi: PrefsApi

    @Inject
    lateinit var imagePickerManager: ImagePickerManager

    private val viewModelProvider by viewModels<UploadKycDocViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentUploadKycDocBinding
        get() = FragmentUploadKycDocBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        initClickListeners()
        observeLiveData()
    }

    private fun setupUI() {
        setupToolbar()

        Glide.with(requireContext()).load(args.kycDoc.icon).into(binding.ivKycDoc)
        binding.tvKycTitle.text = getCustomString(args.kycDoc.getDocType().uploadTitle)
        binding.tvKycDescription.text = getCustomString(args.kycDoc.getDocType().uploadDescription)
        analyticsHandler.postEvent(
            KycConstants.AnalyticsKeys.SHOWN_UPLOAD_DOCUMENT_SCREEN,
            mapOf(KycConstants.AnalyticsKeys.OPTION_CHOSEN to args.kycDoc.title)
        )

    }

    private fun setupToolbar() {
        binding.toolbar.tvTitle.text =
            getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_upload_document)
        binding.toolbar.ivEndImage.setImageResource(R.drawable.feature_kyc_ic_question)
        binding.toolbar.lottieView.isVisible = false
        binding.toolbar.separator.isVisible = true
        binding.toolbar.ivEndImage.isVisible = true

        binding.toolbar.btnBack.setDebounceClickListener {
            popBackStack()
        }

        binding.toolbar.ivEndImage.setDebounceClickListener {
            navigateTo("android-app://com.jar.app/kycFaqFragmentV2", shouldAnimate = true)
        }
    }

    private fun initClickListeners() {
        binding.tvTakeAPhoto.setDebounceClickListener {
            analyticsHandler.postEvent(
                KycConstants.AnalyticsKeys.CLICKED_BUTTON_UPLOAD_DOCUMENT_SCREEN, mapOf(
                    KycConstants.AnalyticsKeys.OPTION_CHOSEN to args.kycDoc.title,
                    KycConstants.AnalyticsKeys.BUTTON to getCustomLocalizedString(
                        requireContext(),
                        com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_take_a_photo,
                        prefsApi.getCurrentLanguageCode()
                    )
                )
            )
            imagePickerManager.openImagePicker(
                ImagePickerOption(
                    docType = args.kycDoc.documentType,
                    cameraType = getCameraTypeForDocument(args.kycDoc.getDocType())
                )
            ) {
                viewModel.postKycOcrRequest(args.kycDoc.documentType, File(it).readBytes())
                popBackStack(R.id.uploadKycDocFragment, false)
            }
        }

        binding.tvChoseFromGallery.setDebounceClickListener {
            analyticsHandler.postEvent(
                KycConstants.AnalyticsKeys.CLICKED_BUTTON_UPLOAD_DOCUMENT_SCREEN, mapOf(
                    KycConstants.AnalyticsKeys.OPTION_CHOSEN to args.kycDoc.title,
                    KycConstants.AnalyticsKeys.BUTTON to getCustomLocalizedString(
                        requireContext(),
                        com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_choose_from_gallery,
                        prefsApi.getCurrentLanguageCode()
                    )
                )
            )
            imagePickerManager.openImagePicker(
                ImagePickerOption(
                    imageSelectionSource = ImageSelectionSource.GALLERY,
                    docType = args.kycDoc.documentType
                ), findNavController()
            ) {
                viewModel.postKycOcrRequest(args.kycDoc.documentType, File(it).readBytes())
                popBackStack(R.id.uploadKycDocFragment, false)
            }
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

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.kycOcrRequestLiveData.collect(onLoading = {
                    showProgressBar()
                }, onSuccess = {
                    dismissProgressBar()
                    it?.let {
                        if (!it.errorImage.isNullOrBlank()) {
                            val encoded = encodeUrl(serializer.encodeToString(it))
                            navigateTo(
                                KycV2NavigationDirections.actionToUploadKycDocFailedFragment(
                                    encoded, args.fromScreen
                                )
                            )
                        } else {
                            navigateTo(
                                KycV2NavigationDirections.actionToUploadSelfieFragment(
                                    it.getDocType(), args.fromScreen
                                )
                            )
                        }
                    }
                }, onError = { message, _ ->
                    dismissProgressBar()
                    message.snackBar(binding.root)
                })
            }
        }
    }
}
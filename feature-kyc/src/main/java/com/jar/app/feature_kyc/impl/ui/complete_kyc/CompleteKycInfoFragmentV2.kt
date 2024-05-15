package com.jar.app.feature_kyc.impl.ui.complete_kyc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_image_picker.api.ImagePickerManager
import com.jar.app.core_image_picker.api.data.ImagePickerOption
import com.jar.app.core_image_picker.api.data.ImageSelectionSource
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_kyc.KycV2NavigationDirections
import com.jar.app.feature_kyc.R
import com.jar.app.feature_kyc.databinding.FragmentCompleteKycInfoV2Binding
import com.jar.app.feature_kyc.impl.ui.upload_options.UploadOptionsBottomSheetFragment
import com.jar.app.feature_kyc.shared.domain.model.DocType
import com.jar.app.feature_kyc.shared.domain.model.KycStatus
import com.jar.app.feature_kyc.shared.domain.model.ManualKycRequest
import com.jar.app.feature_kyc.shared.util.KycConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class CompleteKycInfoFragmentV2 : BaseFragment<FragmentCompleteKycInfoV2Binding>() {

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var imagePickerManager: ImagePickerManager

    @Inject
    lateinit var prefsApi: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    private val args by navArgs<CompleteKycInfoFragmentV2Args>()

    private val fromScreen by lazy { args.fromScreen }

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val viewModelProvider by viewModels<CompleteKycInfoV2ViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCompleteKycInfoV2Binding
        get() = FragmentCompleteKycInfoV2Binding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupToolbar()
        initClickListeners()
        listenUploadOptionSelection()
        observeLiveData()
    }

    private fun listenUploadOptionSelection() {
        setFragmentResultListener(
            UploadOptionsBottomSheetFragment.UPLOAD_OPTION_SELECTION_REQUEST_KEY
        ) { _, bundle ->
            when (bundle.getString(UploadOptionsBottomSheetFragment.OPTION_SELECTION_TYPE)) {
                UploadOptionsBottomSheetFragment.OPTION_CAMERA -> {
                    analyticsHandler.postEvent(
                        KycConstants.AnalyticsKeys.CLICKED_BUTTON_UPLOAD_PAN_CARD_SCREEN,
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
                        ImagePickerOption(docType = DocType.PAN.name)
                    ) {
                        viewModel.postKycOcrRequest(File(it).readBytes())
                        popBackStack(R.id.completeKycInfoFragmentV2, false)
                    }
                }

                UploadOptionsBottomSheetFragment.OPTION_GALLERY -> {
                    analyticsHandler.postEvent(
                        KycConstants.AnalyticsKeys.CLICKED_BUTTON_UPLOAD_PAN_CARD_SCREEN,
                        mapOf(
                            KycConstants.AnalyticsKeys.OPTION_CHOSEN
                                    to getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_choose_from_gallery)
                        )
                    )
                    imagePickerManager.openImagePicker(
                        ImagePickerOption(
                            imageSelectionSource = ImageSelectionSource.GALLERY,
                            docType = DocType.PAN.name
                        )
                    ) {
                        viewModel.postKycOcrRequest(File(it).readBytes())
                        popBackStack(R.id.completeKycInfoFragmentV2, false)
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.tvTitle.text =
            getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_identity_verification)
        binding.toolbar.ivEndImage.setImageResource(R.drawable.feature_kyc_ic_question)
        binding.toolbar.lottieView.isVisible = false
        binding.toolbar.separator.isVisible = true
        binding.toolbar.ivEndImage.isVisible = true

        binding.toolbar.btnBack.setDebounceClickListener {
            analyticsHandler.postEvent(
                KycConstants.AnalyticsKeys.CLICKED_BACK_IDENTITY_VERIFICATION_SCREEN
            )
            popBackStack()
        }

        binding.toolbar.ivEndImage.setDebounceClickListener {
            analyticsHandler.postEvent(
                KycConstants.AnalyticsKeys.SHOWN_FAQ_SECTION_IDENTITY_VERIFICATION_SCREEN,
                mapOf(EventKey.FromScreen to args.fromScreen)
            )
            navigateTo("android-app://com.jar.app/kycFaqFragmentV2", shouldAnimate = true)
        }

        analyticsHandler.postEvent(
            KycConstants.AnalyticsKeys.SHOWN_IDENTITY_VERIFICATION_SCREEN,
            mapOf(EventKey.FromScreen to args.fromScreen)
        )
    }

    private fun initClickListeners() {
        if (fromScreen == BaseConstants.SellGoldFlow.FROM_SELL_GOLD_REVAMP_PAN_ONLY) {
            binding.line.visibility = INVISIBLE
            binding.tvNoPanCard.visibility = INVISIBLE
        }

        binding.tvNoPanCard.setDebounceClickListener {
            analyticsHandler.postEvent(
                KycConstants.AnalyticsKeys.CLICKED_BUTTON_IDENTITY_VERIFICATION_SCREEN,
                mapOf(
                    KycConstants.AnalyticsKeys.OPTION_CHOSEN
                            to getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_i_dont_have_a_pan_card)
                )
            )
            navigateTo(
                CompleteKycInfoFragmentV2Directions.actionCompleteKycInfoFragmentV2ToChooseKycDocFragment(
                    args.fromScreen
                )
            )
        }

        binding.tvUploadPanCard.setDebounceClickListener {
            analyticsHandler.postEvent(
                KycConstants.AnalyticsKeys.CLICKED_BUTTON_IDENTITY_VERIFICATION_SCREEN,
                mapOf(
                    KycConstants.AnalyticsKeys.OPTION_CHOSEN
                            to getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_upload_pan_card)
                )
            )
            val title =
                getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_upload_pan_card)
            navigateTo(KycV2NavigationDirections.actionToUploadOptionsBottomSheetFragment(title))
            analyticsHandler.postEvent(
                KycConstants.AnalyticsKeys.SHOWN_UPLOAD_PAN_CARD_SCREEN
            )
        }

        binding.tvEnterDetailsManually.setDebounceClickListener {
            analyticsHandler.postEvent(
                KycConstants.AnalyticsKeys.CLICKED_BUTTON_IDENTITY_VERIFICATION_SCREEN,
                mapOf(
                    KycConstants.AnalyticsKeys.OPTION_CHOSEN
                            to getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_enter_pan_details_manually)
                )
            )
            navigateTo(
                CompleteKycInfoFragmentV2Directions.actionCompleteKycInfoFragmentV2ToEnterPanDetailsManuallyFragment3(
                    args.fromScreen
                )
            )
        }
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.panOcrRequestLiveData.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            if (it.documentId.isNullOrBlank() || it.dob.isNullOrBlank() || it.name.isNullOrBlank()) {
                                getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_please_upload_a_clear_image_or_upload_manually).snackBar(
                                    binding.root
                                )
                            } else {
                                viewModel.postManualKycRequest(
                                    ManualKycRequest(
                                        panNumber = it.documentId.orEmpty(),
                                        dob = it.dob,
                                        name = it.name
                                    )
                                )
                            }
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.manualKycRequestLiveData.collect(
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
                            navigateTo("android-app://com.jar.app/kycVerificationStatusFragment/$encoded/${args.fromScreen}")
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
}
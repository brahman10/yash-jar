package com.jar.app.feature_kyc.impl.ui.upload_options

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_kyc.databinding.FragmentBottomSheetUploadOptionsBinding
import com.jar.app.feature_kyc.shared.util.KycConstants.AnalyticsKeys.ButtonClicked
import com.jar.app.feature_kyc.shared.util.KycConstants.AnalyticsKeys.ChooseFromGallery
import com.jar.app.feature_kyc.shared.util.KycConstants.AnalyticsKeys.PhotoVerificationDetails_Screen_Clicked
import com.jar.app.feature_kyc.shared.util.KycConstants.AnalyticsKeys.TakePhoto
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class UploadOptionsBottomSheetFragment :
    BaseBottomSheetDialogFragment<FragmentBottomSheetUploadOptionsBinding>() {

    private val args by navArgs<UploadOptionsBottomSheetFragmentArgs>()

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    companion object {
        const val UPLOAD_OPTION_SELECTION_REQUEST_KEY = "uploadOptionSelectionRequestKey"
        const val OPTION_SELECTION_TYPE = "optionSelectionType"
        const val OPTION_CAMERA = "optionCamera"
        const val OPTION_GALLERY = "optionGallery"
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBottomSheetUploadOptionsBinding
        get() = FragmentBottomSheetUploadOptionsBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    override fun setup() {
        setupUI()
        initClickListeners()
    }

    private fun setupUI() {
        binding.tvTitle.text =
            args.title.ifEmpty { getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_upload_document) }
    }

    private fun initClickListeners() {
        binding.ivClose.setDebounceClickListener {
            dismiss()
        }

        binding.tvTakeAPhoto.setDebounceClickListener {
            analyticsHandler.postEvent(PhotoVerificationDetails_Screen_Clicked, ButtonClicked, TakePhoto)
            setFragmentResult(
                UPLOAD_OPTION_SELECTION_REQUEST_KEY,
                bundleOf(Pair(OPTION_SELECTION_TYPE, OPTION_CAMERA))
            )
            dismiss()
        }

        binding.tvChoseFromGallery.setDebounceClickListener {
            analyticsHandler.postEvent(PhotoVerificationDetails_Screen_Clicked, ButtonClicked, ChooseFromGallery)
            setFragmentResult(
                UPLOAD_OPTION_SELECTION_REQUEST_KEY,
                bundleOf(Pair(OPTION_SELECTION_TYPE, OPTION_GALLERY))
            )
            dismiss()
        }
    }
}
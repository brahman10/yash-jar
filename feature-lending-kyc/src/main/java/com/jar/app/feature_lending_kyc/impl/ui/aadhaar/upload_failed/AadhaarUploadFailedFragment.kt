package com.jar.app.feature_lending_kyc.impl.ui.aadhaar.upload_failed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycFragmentAadhaarUploadFailedBinding
import com.jar.app.feature_lending_kyc.impl.data.Step
import com.jar.app.feature_lending_kyc.impl.domain.event.ToolbarStepsVisibilityEvent
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class AadhaarUploadFailedFragment :
    BaseFragment<FeatureLendingKycFragmentAadhaarUploadFailedBinding>() {

    private val args by navArgs<AadhaarUploadFailedFragmentArgs>()

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                popBackStack()
            }
        }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycFragmentAadhaarUploadFailedBinding
        get() = FeatureLendingKycFragmentAadhaarUploadFailedBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(ToolbarNone)
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setClickListener()
        registerBackPressDispatcher()
    }

    private fun setClickListener() {
        binding.btnRetakePhoto.setDebounceClickListener {
            analyticsHandler.postEvent(LendingKycEventKey.Clicked_Button_AadhaarRetakePhotoErrorScreen)
            popBackStack()
        }
    }

    private fun setupUI() {
        binding.tvKycTitle.text = args.kycOcrResponse.title
        binding.tvKycDescription.text = args.kycOcrResponse.description
        Glide.with(requireContext()).load(
            BaseConstants.CDN_BASE_URL + LendingKycConstants.IllustrationUrls.AADHAAR_CARD_NOT_DETECTED_URL
        ).into(binding.ivKycDoc)
        EventBus.getDefault().post(
            ToolbarStepsVisibilityEvent(
                shouldShowSteps = false,
                Step.AADHAAR
            )
        )
        analyticsHandler.postEvent(
            LendingKycEventKey.Shown_OCRPhotoUploadErrorScreen,
            mapOf(
                LendingKycEventKey.optionChosen to LendingKycEventKey.Shown_OCRPhotoUploadErrorScreen,
                LendingKycEventKey.textDisplayed to args.kycOcrResponse.title.orEmpty()
            )
        )
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
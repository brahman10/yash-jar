package com.jar.app.feature_lending_kyc.impl.ui.otp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycBottomSheetTermsAndConditionBinding
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class KycTermsAndConditionBottomSheet :
    BaseBottomSheetDialogFragment<FeatureLendingKycBottomSheetTermsAndConditionBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycBottomSheetTermsAndConditionBinding
        get() = FeatureLendingKycBottomSheetTermsAndConditionBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(shouldShowFullHeight = true)

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args: KycTermsAndConditionBottomSheetArgs by navArgs()

    override fun setup() {
        setupUI()
        setupListener()
    }

    private fun setupUI() {
        analyticsHandler.postEvent(LendingKycEventKey.Shown_ExperianTnCScreenBottomSheet)
        binding.tvTitle.text = args.experianTermsAndCondition.title
        binding.tvDescription.text = args.experianTermsAndCondition.description
    }

    private fun setupListener() {
        binding.ivBack.setDebounceClickListener {
            dismiss()
        }
    }
}
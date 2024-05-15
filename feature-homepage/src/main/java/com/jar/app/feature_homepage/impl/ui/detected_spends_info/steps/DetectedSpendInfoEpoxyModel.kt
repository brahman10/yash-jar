package com.jar.app.feature_homepage.impl.ui.detected_spends_info.steps

import android.view.View
import com.bumptech.glide.Glide
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellDetectedSpendPaymentInfoStepBinding
import com.jar.app.feature_homepage.shared.domain.model.detected_spends.DetectedSpendPaymentInfoStep

internal class DetectedSpendInfoEpoxyModel(
    private val infoStep: DetectedSpendPaymentInfoStep
) :
    CustomViewBindingEpoxyModel<FeatureHomepageCellDetectedSpendPaymentInfoStepBinding>(R.layout.feature_homepage_cell_detected_spend_payment_info_step) {

    override fun bindItem(binding: FeatureHomepageCellDetectedSpendPaymentInfoStepBinding) {
        Glide.with(binding.root).load(infoStep.icon).into(binding.ivImage)
        binding.tvText.text = infoStep.title
    }

    override fun getBinding(view: View): FeatureHomepageCellDetectedSpendPaymentInfoStepBinding {
        return FeatureHomepageCellDetectedSpendPaymentInfoStepBinding.bind(view)
    }
}
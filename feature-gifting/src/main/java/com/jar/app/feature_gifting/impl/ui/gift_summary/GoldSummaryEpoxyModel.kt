package com.jar.app.feature_gifting.impl.ui.gift_summary

import com.jar.app.core_ui.dynamic_cards.base.ViewBindingKotlinModel
import com.jar.app.feature_gifting.R
import com.jar.app.feature_gifting.databinding.FeatureGiftingCellGiftingSummaryBinding
import com.jar.app.feature_gifting.shared.domain.model.GiftSummary

class GoldSummaryEpoxyModel(private val giftSummary: GiftSummary) :
    ViewBindingKotlinModel<FeatureGiftingCellGiftingSummaryBinding>(R.layout.feature_gifting_cell_gifting_summary) {

    override fun FeatureGiftingCellGiftingSummaryBinding.bind() {
        tvKey.text = giftSummary.title
        tvValue.text = giftSummary.value
    }

}
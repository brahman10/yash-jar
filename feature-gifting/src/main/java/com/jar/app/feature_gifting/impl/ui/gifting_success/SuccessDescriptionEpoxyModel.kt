package com.jar.app.feature_gifting.impl.ui.gifting_success

import com.jar.app.core_ui.dynamic_cards.base.ViewBindingKotlinModel
import com.jar.app.feature_gifting.R
import com.jar.app.feature_gifting.databinding.FeatureGiftingCellGiftGoldDescriptionBinding

internal class SuccessDescriptionEpoxyModel(
    private val description: String
) :
    ViewBindingKotlinModel<FeatureGiftingCellGiftGoldDescriptionBinding>(R.layout.feature_gifting_cell_gift_gold_description) {

    override fun FeatureGiftingCellGiftGoldDescriptionBinding.bind() {
        tvDescription.text = description
    }
}
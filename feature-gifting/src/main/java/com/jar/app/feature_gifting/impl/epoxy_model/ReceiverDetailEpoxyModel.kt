package com.jar.app.feature_gifting.impl.epoxy_model

import com.jar.app.core_ui.dynamic_cards.base.ViewBindingKotlinModel
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_gifting.R
import com.jar.app.feature_gifting.databinding.FeatureGiftingCellReceiverDetailBinding
import com.jar.app.feature_gifting.shared.domain.model.ReceiverDetail

internal class ReceiverDetailEpoxyModel(
    private val receiverDetail: ReceiverDetail,
    private val onEditNumberClick: () -> Unit
) :
    ViewBindingKotlinModel<FeatureGiftingCellReceiverDetailBinding>(R.layout.feature_gifting_cell_receiver_detail) {

    override fun FeatureGiftingCellReceiverDetailBinding.bind() {
        tvContactName.text = receiverDetail.name
        tvContactNumber.text = receiverDetail.number

        btnEdit.setDebounceClickListener {
            onEditNumberClick.invoke()
        }
    }

}
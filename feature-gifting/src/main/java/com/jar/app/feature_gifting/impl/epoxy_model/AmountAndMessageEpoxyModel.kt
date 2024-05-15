package com.jar.app.feature_gifting.impl.epoxy_model

import androidx.core.view.isVisible
import com.jar.app.core_ui.dynamic_cards.base.ViewBindingKotlinModel
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_gifting.R
import com.jar.app.feature_gifting.databinding.FeatureGiftingCellAmountDetailBinding
import com.jar.app.feature_gifting.shared.domain.model.AmountAndMessageDetail

internal class AmountAndMessageEpoxyModel(
    private val amountAndMessageDetail: AmountAndMessageDetail,
    private val onEditAmountClick: () -> Unit,
    private val onEditMessageClick: (message: String?) -> Unit
) :
    ViewBindingKotlinModel<FeatureGiftingCellAmountDetailBinding>(R.layout.feature_gifting_cell_amount_detail) {

    override fun FeatureGiftingCellAmountDetailBinding.bind() {
        tvVolumeHeader.text = "I want to send ${amountAndMessageDetail.volumeInGm}gm gold"
        tvValueInRupees.text =
            root.context.getString(
                R.string.feature_gifting_currency_sign_x_float,
                amountAndMessageDetail.amountInRupees
            )

        if (amountAndMessageDetail.message != null) {
            tvAddMessage.isVisible = false
            tvHeaderSpecialMessage.isVisible = true
            tvMessage.isVisible = true
            tvMessage.text = amountAndMessageDetail.message
        } else {
            tvHeaderSpecialMessage.isVisible = false
            tvMessage.isVisible = false
            tvAddMessage.isVisible = true
        }

        btnEdit.setDebounceClickListener {
            onEditAmountClick.invoke()
        }

        tvAddMessage.setDebounceClickListener {
            onEditMessageClick.invoke(amountAndMessageDetail.message)
        }

        tvMessage.setDebounceClickListener {
            onEditMessageClick.invoke(amountAndMessageDetail.message)
        }
    }
}
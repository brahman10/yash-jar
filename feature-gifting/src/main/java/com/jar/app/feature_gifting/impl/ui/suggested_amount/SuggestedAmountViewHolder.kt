package com.jar.app.feature_gifting.impl.ui.suggested_amount

import androidx.core.view.isVisible
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gifting.R
import com.jar.app.feature_gifting.databinding.FeatureGiftingCellSuggestedAmountBinding
import com.jar.app.feature_gifting.shared.util.Constants
import com.jar.app.feature_user_api.domain.model.SuggestedAmount

internal class SuggestedAmountViewHolder(
    private val binding: FeatureGiftingCellSuggestedAmountBinding,
    onSuggestedAmountClick: (suggestedAmount: SuggestedAmount) -> Unit
) :
    BaseViewHolder(binding.root) {

    private var suggestedAmount: SuggestedAmount? = null

    init {
        binding.root.setDebounceClickListener {
            suggestedAmount?.let(onSuggestedAmountClick)
        }
    }

    fun setSuggestedAmount(suggestedAmount: SuggestedAmount) {
        this.suggestedAmount = suggestedAmount
        if (suggestedAmount.unit != null && suggestedAmount.unit!!.contains(Constants.SuggestedAmountUnit.UNIT_GM))
            binding.tvAmount.text = "${suggestedAmount.amount} ${suggestedAmount.unit}"
        else
            binding.tvAmount.text =
                context.getString(
                    R.string.feature_gifting_currency_sign_x_int,
                    suggestedAmount.amount.toInt()
                )
        binding.tvBest.isVisible = suggestedAmount.recommended.orFalse()
    }
}
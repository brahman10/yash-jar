package com.jar.app.feature_gold_sip.impl.ui.update_sip.adapter.suggestion

import androidx.core.view.isVisible
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipCellSuggestedAmountBinding
import com.jar.app.feature_user_api.domain.model.SuggestedAmount

internal class SipAmountSuggestionViewHolder(
    private val binding: FeatureGoldSipCellSuggestedAmountBinding,
    private val onItemClick: (SuggestedAmount, Int) -> Unit
) :
    BaseViewHolder(binding.root) {
    private var suggestedAmount:SuggestedAmount? = null

    init {
        binding.root.setDebounceClickListener {
            suggestedAmount?.let { onItemClick.invoke(it, bindingAdapterPosition) }
        }
    }

    fun setSuggestedAmount(suggestedAmount: SuggestedAmount) {
        this.suggestedAmount = suggestedAmount
        binding.tvAmount.text = suggestedAmount.amount.toInt().toString()
        binding.clBestContainer.isVisible = suggestedAmount.recommended.orFalse()
    }
}
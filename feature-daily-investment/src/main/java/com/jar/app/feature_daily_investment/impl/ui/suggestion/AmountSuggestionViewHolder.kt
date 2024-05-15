package com.jar.app.feature_daily_investment.impl.ui.suggestion

import androidx.core.view.isVisible
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentCellSuggestedGoldAmountBinding
import com.jar.app.feature_savings_common.shared.domain.model.SavingSuggestedAmount

internal class AmountSuggestionViewHolder(
    private val binding: FeatureDailyInvestmentCellSuggestedGoldAmountBinding,
    onSuggestedAmountClick: (suggestedAmount: SavingSuggestedAmount) -> Unit
) : BaseViewHolder(binding.root) {

    private var suggestedAmount: SavingSuggestedAmount? = null

    init {
        binding.root.setDebounceClickListener {
            suggestedAmount?.let(onSuggestedAmountClick)
        }
    }

    fun setSuggestedAmount(suggestedAmount: SavingSuggestedAmount) {
        this.suggestedAmount = suggestedAmount
        binding.tvAmount.text =
            context.getString(
                R.string.feature_daily_investment_rupee_x_in_int,
                suggestedAmount.amount.toInt()
            )
        binding.flBestHolder.isVisible = suggestedAmount.recommended
    }

}
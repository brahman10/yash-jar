package com.jar.app.feature_daily_investment.impl.ui

import androidx.core.view.isVisible
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.shared.domain.model.SuggestedRecurringAmount
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentCellSuggestedGoldAmountBinding

internal class SuggestedAmountViewHolder(
    private val binding: FeatureDailyInvestmentCellSuggestedGoldAmountBinding,
    onSuggestedAmountClick: (suggestedAmount: SuggestedRecurringAmount) -> Unit
) :
    BaseViewHolder(binding.root) {

    private var suggestedAmount: SuggestedRecurringAmount? = null

    init {
        binding.root.setDebounceClickListener {
            suggestedAmount?.let(onSuggestedAmountClick)
        }
    }

    fun setSuggestedAmount(suggestedAmount: SuggestedRecurringAmount) {
        this.suggestedAmount = suggestedAmount
        binding.tvAmount.text =
            context.getString(
                R.string.feature_daily_investment_rupee_x_in_int,
                suggestedAmount.amount.toInt()
            )
        binding.flBestHolder.isVisible = suggestedAmount.recommended
    }

}
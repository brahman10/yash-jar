package com.jar.app.feature_daily_investment.impl.ui.suggestion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentCellSuggestedGoldAmountBinding
import com.jar.app.feature_savings_common.shared.domain.model.SavingSuggestedAmount

internal class AmountSuggestionAdapter (
    private val onSuggestedAmountClick: (suggestedAmount: SavingSuggestedAmount) -> Unit
) : ListAdapter<SavingSuggestedAmount, AmountSuggestionViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SavingSuggestedAmount>() {
            override fun areItemsTheSame(
                oldItem: SavingSuggestedAmount,
                newItem: SavingSuggestedAmount
            ): Boolean {
                return oldItem.amount == newItem.amount
            }

            override fun areContentsTheSame(
                oldItem: SavingSuggestedAmount,
                newItem: SavingSuggestedAmount
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmountSuggestionViewHolder {
        val binding = FeatureDailyInvestmentCellSuggestedGoldAmountBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AmountSuggestionViewHolder(binding, onSuggestedAmountClick)
    }

    override fun onBindViewHolder(holder: AmountSuggestionViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setSuggestedAmount(it)
        }
    }

}
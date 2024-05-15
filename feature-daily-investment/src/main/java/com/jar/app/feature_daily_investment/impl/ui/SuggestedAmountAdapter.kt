package com.jar.app.feature_daily_investment.impl.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentCellSuggestedGoldAmountBinding
import com.jar.app.feature_daily_investment.shared.domain.model.SuggestedRecurringAmount

internal class SuggestedAmountAdapter(
    private val onSuggestedAmountClick: (suggestedAmount: SuggestedRecurringAmount) -> Unit
) :
    ListAdapter<SuggestedRecurringAmount, SuggestedAmountViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SuggestedRecurringAmount>() {
            override fun areItemsTheSame(
                oldItem: SuggestedRecurringAmount,
                newItem: SuggestedRecurringAmount
            ): Boolean {
                return oldItem.amount == newItem.amount
            }

            override fun areContentsTheSame(
                oldItem: SuggestedRecurringAmount,
                newItem: SuggestedRecurringAmount
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestedAmountViewHolder {
        val binding = FeatureDailyInvestmentCellSuggestedGoldAmountBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SuggestedAmountViewHolder(binding, onSuggestedAmountClick)
    }

    override fun onBindViewHolder(holder: SuggestedAmountViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setSuggestedAmount(it)
        }
    }

}
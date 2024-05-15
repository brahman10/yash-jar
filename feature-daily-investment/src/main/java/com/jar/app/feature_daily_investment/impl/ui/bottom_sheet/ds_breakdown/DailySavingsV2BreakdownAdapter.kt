package com.jar.app.feature_daily_investment.impl.ui.bottom_sheet.ds_breakdown

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_daily_investment.shared.domain.model.Details
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentDailySavingsBreakdownViewBinding

internal class DailySavingsBreakdownAdapter:
    ListAdapter<Details, DailySavingsBreakdownViewHolder>(DIFF_CALLBACK){

        companion object {
            private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Details>() {
                override fun areItemsTheSame(oldItem: Details, newItem: Details): Boolean {
                    return oldItem.label == newItem.label
                }

                override fun areContentsTheSame(oldItem: Details, newItem: Details): Boolean {
                    return oldItem == newItem
                }
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): DailySavingsBreakdownViewHolder {
            val binding = FeatureDailyInvestmentDailySavingsBreakdownViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return DailySavingsBreakdownViewHolder(binding)
        }

        override fun onBindViewHolder(holder: DailySavingsBreakdownViewHolder, position: Int) {
            getItem(position)?.let {
                holder.bind(it)
            }
        }
    }

internal class DailySavingsBreakdownViewHolder(
    private val binding: FeatureDailyInvestmentDailySavingsBreakdownViewBinding,
) :
    BaseViewHolder(binding.root) {

    fun bind(data: Details){
        binding.tvLabel.setTextColor(ContextCompat.getColor(context, data.color))
        binding.tvLabel.text = data.label
        binding.tvValue.setTextColor(ContextCompat.getColor(context, data.color))
        binding.tvValue.text = data.value
    }

}


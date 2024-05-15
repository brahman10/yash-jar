package com.jar.app.feature_daily_investment.impl.ui.bottom_sheet.abandonScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_daily_investment.shared.domain.model.Steps
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentAbandonScreenRvViewBinding


internal class DailySavingsV2AbandonAdapter(
) :
   ListAdapter<Steps, DailySavingsV2AbandonViewHolder>(DIFF_CALLBACK){

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Steps>() {
            override fun areItemsTheSame(oldItem: Steps, newItem: Steps): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: Steps, newItem: Steps): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DailySavingsV2AbandonViewHolder {
        val binding = FeatureDailyInvestmentAbandonScreenRvViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailySavingsV2AbandonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailySavingsV2AbandonViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}
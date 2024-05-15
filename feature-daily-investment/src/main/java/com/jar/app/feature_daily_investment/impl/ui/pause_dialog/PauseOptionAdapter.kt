package com.jar.app.feature_daily_investment.impl.ui.pause_dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_daily_investment.shared.domain.model.PauseDailySavingData
import com.jar.app.feature_daily_investment.databinding.FeatureDailyCellPauseAutoInvestTimeBinding

class PauseOptionAdapter(
    private val onClick: (pauseDailySavingData: PauseDailySavingData) -> Unit
) :
    ListAdapter<PauseDailySavingData, PauseOptionViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PauseDailySavingData>() {
            override fun areItemsTheSame(
                oldItem: PauseDailySavingData,
                newItem: PauseDailySavingData
            ): Boolean {
                return oldItem.pauseDailySavingsOption.timeValue == newItem.pauseDailySavingsOption.timeValue
            }

            override fun areContentsTheSame(
                oldItem: PauseDailySavingData,
                newItem: PauseDailySavingData
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PauseOptionViewHolder {
        val binding = FeatureDailyCellPauseAutoInvestTimeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PauseOptionViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: PauseOptionViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setPauseOption(it)
        }
    }
}
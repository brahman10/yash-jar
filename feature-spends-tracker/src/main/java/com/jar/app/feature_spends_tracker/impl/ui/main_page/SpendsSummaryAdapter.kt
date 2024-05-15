package com.jar.app.feature_spends_tracker.impl.ui.main_page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_spends_tracker.databinding.FeatureSpendsTrackerCellSpendsSummaryBinding
import com.jar.app.feature_spends_tracker.shared.domain.model.spendsDetailsData.SpendsData

class SpendsSummaryAdapter(private val listener: RvSummaryClickListener) : ListAdapter<SpendsData, SpendsSummaryViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object: DiffUtil.ItemCallback<SpendsData>(){

            override fun areItemsTheSame(oldItem: SpendsData, newItem: SpendsData): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: SpendsData, newItem: SpendsData): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpendsSummaryViewHolder {
        return SpendsSummaryViewHolder(
            FeatureSpendsTrackerCellSpendsSummaryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )
    }

    override fun onBindViewHolder(holder: SpendsSummaryViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setSpendsSummary(it)
        }
    }
}
package com.jar.android.feature_post_setup.impl.ui.failed_transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.android.feature_post_setup.databinding.FeaturePostSetupCellCalenderFailedDayBinding
import com.jar.app.feature_post_setup.domain.model.calendar.FeaturePostSetUpCalendarInfo

internal class FailedDayTransactionAdapter(private val onItemClick: (FeaturePostSetUpCalendarInfo, Int) -> Unit) :
    ListAdapter<FeaturePostSetUpCalendarInfo, FailedDayTransactionViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FeaturePostSetUpCalendarInfo>() {
            override fun areItemsTheSame(oldItem: FeaturePostSetUpCalendarInfo, newItem: FeaturePostSetUpCalendarInfo): Boolean {
                return oldItem.day == newItem.day
            }

            override fun areContentsTheSame(oldItem: FeaturePostSetUpCalendarInfo, newItem: FeaturePostSetUpCalendarInfo): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        FailedDayTransactionViewHolder(
            FeaturePostSetupCellCalenderFailedDayBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            onItemClick
        )

    override fun onBindViewHolder(holder: FailedDayTransactionViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

}
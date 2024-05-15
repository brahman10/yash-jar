package com.jar.android.feature_post_setup.impl.ui.setup_details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.android.feature_post_setup.impl.ui.setup_details.viewholders.SetupCalenderDayViewHolder
import com.jar.app.core_ui.databinding.FeaturePostSetupCellCalenderDayBinding
import com.jar.app.feature_post_setup.domain.model.calendar.FeaturePostSetUpCalendarInfo

internal class SetupCalenderDayAdapter(private val onItemClick: (FeaturePostSetUpCalendarInfo) -> Unit) :
    ListAdapter<FeaturePostSetUpCalendarInfo, SetupCalenderDayViewHolder>(DIFF_CALLBACK) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SetupCalenderDayViewHolder(
        FeaturePostSetupCellCalenderDayBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onItemClick
    )

    override fun onBindViewHolder(holder: SetupCalenderDayViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

}
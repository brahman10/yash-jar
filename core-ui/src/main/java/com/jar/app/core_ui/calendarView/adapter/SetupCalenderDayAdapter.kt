package com.jar.app.core_ui.calendarView.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.calendarView.model.CalendarInfo
import com.jar.app.core_ui.calendarView.viewholder.SetupCalenderDayViewHolder
import com.jar.app.core_ui.databinding.CellCalenderBinding
import com.jar.app.core_ui.databinding.FeaturePostSetupCellCalenderDayBinding

internal class SetupCalenderDayAdapter(private val onItemClick: (CalendarInfo) -> Unit) :
    ListAdapter<CalendarInfo, SetupCalenderDayViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CalendarInfo>() {
            override fun areItemsTheSame(oldItem: CalendarInfo, newItem: CalendarInfo): Boolean {
                return oldItem.day == newItem.day
            }

            override fun areContentsTheSame(oldItem: CalendarInfo, newItem: CalendarInfo): Boolean {
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
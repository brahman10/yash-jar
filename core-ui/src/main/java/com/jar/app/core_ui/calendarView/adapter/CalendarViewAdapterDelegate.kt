package com.jar.app.core_ui.calendarView.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.core_ui.calendarView.CalendarViewHolder
import com.jar.app.core_ui.calendarView.model.CalendarInfo
import com.jar.app.core_ui.calendarView.model.SavingOperations
import com.jar.app.core_ui.calendarView.viewholder.CalenderViewPageItem
import com.jar.app.core_ui.calendarView.viewholder.PostSetupPageItem
import com.jar.app.core_ui.databinding.CellCalenderBinding
import kotlinx.coroutines.CoroutineScope

class CalendarViewAdapterDelegate(
    private val uiScope: CoroutineScope,
    private val onDayClick: (CalendarInfo) -> Unit,
    private val onNextMonthClicked: () -> Unit,
    private val onPrevMonthClicked: () -> Unit,
    private val onDSOperationCtaClicked: (SavingOperations) -> Unit,
) : AdapterDelegate<List<PostSetupPageItem>>() {

    override fun isForViewType(items: List<PostSetupPageItem>, position: Int): Boolean {
        return items[position] is CalenderViewPageItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = CellCalenderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CalendarViewHolder(
            binding,
            uiScope,
            onDayClick,
            onNextMonthClicked,
            onPrevMonthClicked,
            onDSOperationCtaClicked
        )
    }

    override fun onBindViewHolder(
        items: List<PostSetupPageItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (holder is CalendarViewHolder && item is CalenderViewPageItem)
            holder.setupCalendar(item)
    }
}
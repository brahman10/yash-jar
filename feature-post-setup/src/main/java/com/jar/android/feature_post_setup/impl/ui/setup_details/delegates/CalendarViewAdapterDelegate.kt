package com.jar.android.feature_post_setup.impl.ui.setup_details.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.android.feature_post_setup.databinding.FeaturePostSetupCellCalenderBinding
import com.jar.android.feature_post_setup.impl.ui.setup_details.viewholders.CalendarViewHolder
import com.jar.app.feature_post_setup.domain.model.PostSetupPageItem
import com.jar.app.feature_post_setup.domain.model.CalenderViewPageItem
import com.jar.app.feature_post_setup.domain.model.calendar.FeaturePostSetUpCalendarInfo
import com.jar.app.feature_post_setup.domain.model.calendar.SavingOperations
import kotlinx.coroutines.CoroutineScope

internal class CalendarViewAdapterDelegate(
    private val uiScope: CoroutineScope,
    private val onDayClick: (FeaturePostSetUpCalendarInfo) -> Unit,
    private val onNextMonthClicked: () -> Unit,
    private val onPrevMonthClicked: () -> Unit,
    private val onDSOperationCtaClicked: (SavingOperations) -> Unit,
) : AdapterDelegate<List<PostSetupPageItem>>() {

    override fun isForViewType(items: List<PostSetupPageItem>, position: Int): Boolean {
        return items[position] is CalenderViewPageItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = FeaturePostSetupCellCalenderBinding.inflate(
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
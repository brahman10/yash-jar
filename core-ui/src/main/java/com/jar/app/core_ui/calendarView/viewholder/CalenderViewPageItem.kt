package com.jar.app.core_ui.calendarView.viewholder

import com.jar.app.core_ui.calendarView.model.CalendarInfo
import com.jar.app.core_ui.calendarView.model.CalendarSavingOperations

data class CalenderViewPageItem(
    val order: Int,
    val calendarInfo: List<CalendarInfo>,
    val calendarSavingOperations: CalendarSavingOperations?,
    val yearAndMonthText: String,
    val isPreviousClickEnabled: Boolean = true,
    val isNextClickEnabled: Boolean = true,
) : PostSetupPageItem {
    override fun getSortKey(): Int {
        return order
    }
}

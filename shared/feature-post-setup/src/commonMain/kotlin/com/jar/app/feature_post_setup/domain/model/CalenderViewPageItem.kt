package com.jar.app.feature_post_setup.domain.model

import com.jar.app.feature_post_setup.domain.model.calendar.FeaturePostSetUpCalendarInfo
import com.jar.app.feature_post_setup.domain.model.calendar.CalendarSavingOperations
import com.jar.app.feature_post_setup.domain.model.calendar.LadderData

data class CalenderViewPageItem(
    val order: Int,
    val calendarInfo: List<FeaturePostSetUpCalendarInfo>,
    val calendarSavingOperations: CalendarSavingOperations?,
    val yearAndMonthText: String,
    val isPreviousClickEnabled: Boolean = true,
    val isNextClickEnabled: Boolean = true,
    val ladderData: LadderData? = null
) : PostSetupPageItem {
    override fun getSortKey(): Int {
        return order
    }
}

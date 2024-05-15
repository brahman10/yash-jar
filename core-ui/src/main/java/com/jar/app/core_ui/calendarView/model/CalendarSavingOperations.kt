package com.jar.app.core_ui.calendarView.model

import kotlinx.serialization.SerialName


@kotlinx.serialization.Serializable
data class CalendarSavingOperations(
    @SerialName("quickActionList")
    val savingOperations:List<SavingOperations>
)

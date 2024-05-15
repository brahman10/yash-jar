package com.jar.app.feature_post_setup.domain.model.calendar

import kotlinx.serialization.SerialName


@kotlinx.serialization.Serializable
data class CalendarSavingOperations(
    @SerialName("quickActionList")
    val savingOperations:List<SavingOperations>
)

package com.jar.app.core_ui.calendarView.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class CalendarInfo(
    @SerialName("id")
    val id: String? = null,

    @SerialName("day")
    val day: Int,

    @SerialName("status")
    val status: String,

    @SerialName("amount")
    val amount: Float? = null,

    @SerialName("isSelected")
    var isSelected: Boolean? = null
)


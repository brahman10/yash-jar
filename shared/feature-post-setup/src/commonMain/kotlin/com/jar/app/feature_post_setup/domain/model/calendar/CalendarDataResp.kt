package com.jar.app.feature_post_setup.domain.model.calendar

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class CalendarDataResp(
    @SerialName("successInfo")
    val successInfo: AmountInfo? = null,

    @SerialName("failureInfo")
    val failureInfo: AmountInfo? = null,

    @SerialName("pendingInfo")
    val pendingInfo: AmountInfo? = null,

    @SerialName("calendarInfo")
    val calendarInfo: List<FeaturePostSetUpCalendarInfo>,

    @SerialName("ladderData")
    val ladderData: LadderData? = null,
)
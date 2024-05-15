package com.jar.app.feature_daily_investment_cancellation.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DailyInvestmentPauseDetails(
    @SerialName("title")
    val title: String? = null,

    @SerialName("pauseDaysMap")
    val pauseDaysMap: Map<Int,String>? = null,

    @SerialName("buttonText")
    val buttonText: String? = null,

    @SerialName("calendarViewThreshold")
    val calendarViewThreshold: String? = null,

    @SerialName("subVersion")
    var subVersion: String? = null,
)


@kotlinx.serialization.Serializable
data class DailyInvestmentPauseDateList(
    @SerialName("noOfDay")
    val noOfDay: Int,

    @SerialName("tillDate")
    val tillDate: String,

    @SerialName("isSelected")
    val isSelected: Boolean
)

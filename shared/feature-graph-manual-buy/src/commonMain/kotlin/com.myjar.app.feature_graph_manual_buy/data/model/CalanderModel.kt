package com.myjar.app.feature_graph_manual_buy.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CalanderModel(
    @SerialName("dayResponses")
    val dayResponses: List<DayResponse>? = null,

    @SerialName("leftSwipeEnable")
    val leftSwipeEnable: Boolean? = null,

    @SerialName("rightSwipeEnable")
    val rightSwipeEnable: Boolean? = null
)

@Serializable
data class DayResponse(
    @SerialName("day")
    val day: Int,

    @SerialName("weekDay")
    val weekDay: Int,

    @SerialName("amount")
    val amount: Float? = null,

    @SerialName("color")
    val color: String,

    @SerialName("status")
    val status: String
)

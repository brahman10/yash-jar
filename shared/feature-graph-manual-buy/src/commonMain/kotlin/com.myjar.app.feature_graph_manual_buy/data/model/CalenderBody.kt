package com.myjar.app.feature_graph_manual_buy.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CalenderBody(
    @SerialName("startDate")
    val startDate: String,
    @SerialName("endDate")
    val endDate: String
)
package com.jar.app.feature_spends_tracker.shared.domain.model.spendsDetailsData


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpendsGraphDetails(
    @SerialName("spendsComparisonText")
    val spendsComparisonText: String,
    @SerialName("subHeader")
    val subHeader: String,
    @SerialName("xaxis")
    val xaxis: List<String>,
    @SerialName("xaxisValues")
    val xaxisValues: List<Int>,
    @SerialName("yaxis")
    val yaxis: List<Int>
)
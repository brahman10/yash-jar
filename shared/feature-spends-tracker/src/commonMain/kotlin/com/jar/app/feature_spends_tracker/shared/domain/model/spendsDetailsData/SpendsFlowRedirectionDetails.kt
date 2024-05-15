package com.jar.app.feature_spends_tracker.shared.domain.model.spendsDetailsData


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpendsFlowRedirectionDetails(
    @SerialName("buttonLink")
    val buttonLink: String,
    @SerialName("buttonText")
    val buttonText: String,
    @SerialName("buyGoldIcon")
    val buyGoldIcon: String,
    @SerialName("headers")
    val headers: String,
    @SerialName("spendsTrackerEducationinfo")
    val spendsTrackerEducationinfo: String,
    @SerialName("spendsTransactionText")
    val spendsTransactionText: String,
    @SerialName("monthText")
    val monthText: String
)
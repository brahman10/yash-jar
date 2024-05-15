package com.jar.app.feature_spends_tracker.shared.domain.model.spendsDetailsData


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpendsData(
    @SerialName("spendsFlowRedirectionDetails")
    val spendsFlowRedirectionDetails: SpendsFlowRedirectionDetails,
    @SerialName("spendsGraphDetails")
    val spendsGraphDetails: SpendsGraphDetails,
    @SerialName("spendsTrackerResponseSummary")
    val spendsTrackerResponseSummary: SpendsTrackerResponseSummary
)
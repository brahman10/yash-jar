package com.jar.app.feature_spends_tracker.shared.domain.model.spends_education


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpendsTrackerEducationInfo(
    @SerialName("infoIcon")
    val infoIcon: String,
    @SerialName("infoText")
    val infoText: String
)
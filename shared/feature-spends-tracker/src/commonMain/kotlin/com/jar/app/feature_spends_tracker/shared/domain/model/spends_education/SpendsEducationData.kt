package com.jar.app.feature_spends_tracker.shared.domain.model.spends_education


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpendsEducationData(
    @SerialName("ctaDeeplLink")
    val ctaDeeplLink: String? = null,
    @SerialName("ctaText")
    val ctaText: String,
    @SerialName("header")
    val header: String,
    @SerialName("infoBGColor")
    val infoBGColor: String,
    @SerialName("introducingText")
    val introducingText: String,
    @SerialName("privacyInfoIcon")
    val privacyInfoIcon: String,
    @SerialName("privacyInfoString")
    val privacyInfoString: String,
    @SerialName("spendsTrackerEducationInfo")
    val spendsTrackerEducationInfo: List<SpendsTrackerEducationInfo>,
    @SerialName("spendsTrackerIcon")
    val spendsTrackerIcon: String
)
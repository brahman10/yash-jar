package com.jar.app.feature_health_insurance.shared.data.models.landing1

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LandingVideoSection(
    @SerialName("headerText")
    val headerText: String? = null,
    @SerialName("videos")
    val videos: List<LandingVideo>? = null,
)

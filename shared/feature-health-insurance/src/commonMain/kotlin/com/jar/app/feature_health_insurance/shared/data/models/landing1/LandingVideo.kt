package com.jar.app.feature_health_insurance.shared.data.models.landing1

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LandingVideo(
    @SerialName("header")
    val header: String? = null,
    @SerialName("videoUrl")
    val videoUrl: String? = null,
    @SerialName("leftIconUrl")
    val leftIconUrl: String? = null,
    @SerialName("rightIconUrl")
    val rightIconUrl: String? = null,
)

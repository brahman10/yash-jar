package com.jar.app.feature_health_insurance.shared.data.models.landing1

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DefaultVideo (
    @SerialName("thumbnailUrl")
    val thumbNailUrl: String? = null,
    @SerialName("videoUrl")
    val videoUrl: String? = null
)


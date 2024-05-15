package com.jar.app.feature_homepage.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InAppReviewStatus(
    @SerialName("success")
    val success: Boolean? = null,
    @SerialName("showRatingScreen")
    val showRatingScreen: Boolean
)

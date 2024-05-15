package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LandingScreenContent(
    @SerialName("title")
    val title: String,
    @SerialName("subTitle")
    val subTitle: String,
    @SerialName("slider")
    val slider: List<LandingSlider>,
    @SerialName("newLandingScreenFAQs")
    val newLandingScreenFAQs: List<QuestionAnswer>,
    @SerialName("trustCount")
    val trustCount: String
)

@Serializable
data class LandingScreenContentResponse(
    @SerialName("landingScreenContent")
    val landingScreenContent: LandingScreenContent
)

@Serializable
data class LandingSlider(
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String
)

package com.jar.app.feature_daily_investment.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DSOnboardingStoryData(
    @SerialName("stories")
    val stories: List<Stories>,

    @SerialName("storyType")
    val storyType: String? = null,
)

@Serializable
data class Stories(
    @SerialName("title")
    val title: String,

    @SerialName("image")
    val image: String,

    @SerialName("description")
    val description: String? = null,
)
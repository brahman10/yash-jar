package com.jar.app.core_ui.onboarding_stories.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OnboardingStoryData(
    @SerialName("stories")
    val stories: List<Stories>? = null,

    @SerialName("storyType")
    val storyType: String? = null,
)

@Serializable
data class Stories(
    @SerialName("title")
    val title: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("image")
    val image: String? = null,
)
package com.jar.app.feature_onboarding.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class OnboardingStoryData(
    @SerialName("stories")
    val stories: List<Stories>,

    @SerialName("storyType")
    val storyType: String? = null,

    @SerialName("variant")
    val variant: String? = null,
): Parcelable

@Serializable
@Parcelize
data class Stories(
    @SerialName("title")
    val title: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("image")
    val image: String? = null,

    @SerialName("bgImage")
    val bgImage: String? = null,

    @SerialName("buttonTextInitial")
    val buttonTextInitial: String? = null,

    @SerialName("buttonTextFinal")
    val buttonTextFinal: String? = null,

    @SerialName("textColor")
    val textColor: String? = null,
): Parcelable

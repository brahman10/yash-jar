package com.jar.app.feature_onboarding.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class OnboardingStory(
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("title")
    val title: String
) : Parcelable
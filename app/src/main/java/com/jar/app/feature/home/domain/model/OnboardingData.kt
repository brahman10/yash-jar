package com.jar.app.feature.home.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class OnboardingData(
    @SerialName("campaign")
    val campaign: String? = null,

    @SerialName("deepLink")
    val deepLink: String? = null
)
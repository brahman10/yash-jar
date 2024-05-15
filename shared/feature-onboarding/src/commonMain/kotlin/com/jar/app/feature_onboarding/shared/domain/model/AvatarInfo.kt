package com.jar.app.feature_onboarding.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AvatarInfo(
    @SerialName("image")
    val image: String,

    @SerialName("default")
    val default: Boolean? = null
)
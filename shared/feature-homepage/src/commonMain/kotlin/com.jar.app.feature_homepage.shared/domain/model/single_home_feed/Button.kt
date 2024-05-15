package com.jar.app.feature_homepage.shared.domain.model.single_home_feed

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Button(
    @SerialName("actionType")
    val actionType: String? = null,

    @SerialName("deepLink")
    val deepLink: String,

    @SerialName("endColor")
    val endColor: String,

    @SerialName("icon")
    val icon: String? = null,

    @SerialName("startColor")
    val startColor: String,

    @SerialName("text")
    val text: ButtonText
)
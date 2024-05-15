package com.jar.app.feature_homepage.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class QuickActionData(
    @SerialName("title")
    val title: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("icon")
    val iconUrl: String?,
    @SerialName("deepLink")
    val deeplink: String?,
    @SerialName("cardType")
    val cardType: String,
    @SerialName("isPrimary")
    val isPrimary: Boolean? = false
)
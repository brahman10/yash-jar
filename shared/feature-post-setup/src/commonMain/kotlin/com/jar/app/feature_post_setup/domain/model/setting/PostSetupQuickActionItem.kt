package com.jar.app.feature_post_setup.domain.model.setting

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostSetupQuickActionItem(
    @SerialName("icon")
    val icon: String,
    @SerialName("title")
    val title: String,
    @SerialName("deepLink")
    val deeplink: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("cardType")
    val cardType: String? = null
)

package com.jar.app.feature_post_setup.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostSetupDailySavingsInfo(
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("bgColor")
    val bgColor: String,
)

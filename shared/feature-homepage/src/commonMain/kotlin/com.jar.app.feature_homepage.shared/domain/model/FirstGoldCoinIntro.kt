package com.jar.app.feature_homepage.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class FirstGoldCoinIntro(
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("lottieUrl")
    val lottieUrl: String
)
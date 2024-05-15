package com.jar.app.feature_homepage.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class HelpVideosResponse(
    @SerialName("contentType")
    val contentType: String,
    @SerialName("helpVideoData")
    val helpVideoData: List<HelpVideo>
)

@kotlinx.serialization.Serializable
data class HelpVideo(
    @SerialName("link")
    val link: String,
    @SerialName("lengthInSeconds")
    val lengthInSeconds: String,
    @SerialName("title")
    val title: String,
    @SerialName("thumbnail")
    val thumbnail: String,
)
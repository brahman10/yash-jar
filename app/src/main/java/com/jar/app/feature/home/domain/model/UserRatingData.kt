package com.jar.app.feature.home.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UserRatingData(
    @SerialName("title")
    val title: String,

    @SerialName("subTitle")
    val subTitle: String
)
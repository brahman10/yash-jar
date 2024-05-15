package com.jar.app.feature_homepage.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DailySavingsV2CardResponse(

    @SerialName("header")
    val header: String,

    @SerialName("title")
    val title: String,

    @SerialName("desc")
    val desc: String,

    @SerialName("buttonText")
    val buttonText: String,

    @SerialName("jarImage")
    val jarImage: String?,

    @SerialName("cashbackText")
    val cashbackText: String?,

    @SerialName("knowMoreText")
    val knowMoreText: String?,

    @SerialName("imageUrl")
    val rupeeImage: String?,
)

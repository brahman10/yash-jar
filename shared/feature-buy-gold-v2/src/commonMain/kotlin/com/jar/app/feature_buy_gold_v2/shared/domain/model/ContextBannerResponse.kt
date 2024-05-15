package com.jar.app.feature_buy_gold_v2.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContextBannerResponse(
    @SerialName("iconDetails")
    val iconDetails: IconDetails,

    @SerialName("header")
    val header: String,

    @SerialName("description")
    val description: String,

    @SerialName("bgStartColor")
    val bgStartColor: String,

    @SerialName("bgEndColor")
    val bgEndColor: String,

    @SerialName("headerColor")
    val headerColor: String,

    @SerialName("descColor")
    val descColor: String,

    @SerialName("timerDetails")
    var timerDetails: BannerTimerDetails? = null,
    @SerialName("bannerType")
    var bannerType: String? = null
)

@Serializable
data class IconDetails(
    @SerialName("icon")
    val icon: String,

    @SerialName("iconMediaType")
    val iconMediaType: MediaType
)

@Serializable
data class BannerTimerDetails(
    @SerialName("time")
    val time: String,

    @SerialName("desc")
    val desc: String,

    @SerialName("bgColor")
    val bgColor: String,

    @SerialName("descColor")
    val descColor: String
)

enum class MediaType {
    IMAGE,
    LOTTIE
}
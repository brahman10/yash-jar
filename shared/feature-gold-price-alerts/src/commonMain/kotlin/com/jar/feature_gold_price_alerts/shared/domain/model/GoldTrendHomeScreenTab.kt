package com.jar.feature_gold_price_alerts.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoldTrendHomeScreenTab(
    @SerialName("title")
    val title: String,
    @SerialName("infographic")
    val infographic: GoldTrendHomeScreenTabInfographic,
    //Only used for analytics as of now
    @SerialName("pillType")
    val pillType: String? = null,
)

@Serializable
data class GoldTrendHomeScreenTabInfographic(
    @SerialName("type")
    private val type: String,
    @SerialName("url")
    val url: String,
    @SerialName("lottieSpeedScale")
    private val lottieSpeedScale: Float?
) {
    fun getInfographicType() = GoldTrendHomeScreenTabInfographicType.values().find { it.name == type }

    fun getLottieSpeed() = lottieSpeedScale ?: 0.5f
}

enum class GoldTrendHomeScreenTabInfographicType {
    IMAGE,
    LOTTIE
}
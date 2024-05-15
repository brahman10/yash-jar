package com.jar.feature_gold_price_alerts.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoldTrendTopRibbon(
    @SerialName("title")
    val title: String,
    @SerialName("iconUrl")
    val iconUrl: String,
    @SerialName("startColor")
    val startColor: String? = null,
    @SerialName("endColor")
    val endColor: String? = null
)
package com.jar.feature_gold_price_alerts.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoldTrendPricePill(
    @SerialName("price")
    val price: Float,
    @SerialName("pillText")
    val pillText: String? = null,
    @SerialName("pillColor")
    val pillColor: String? = null,

    //For UI
    val isSelected: Boolean = false
)
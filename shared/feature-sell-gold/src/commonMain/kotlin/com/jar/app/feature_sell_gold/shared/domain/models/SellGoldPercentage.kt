package com.jar.app.feature_sell_gold.shared.domain.models

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SellGoldPercentage(
    @SerialName("amount")
    val amount: Float,
    @SerialName("recommended")
    val recommended: Boolean
)
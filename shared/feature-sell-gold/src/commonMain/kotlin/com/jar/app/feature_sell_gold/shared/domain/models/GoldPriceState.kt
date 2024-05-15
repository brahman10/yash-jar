package com.jar.app.feature_sell_gold.shared.domain.models

data class GoldPriceState(
    val rateId: String = "",
    val rateValidity: String = "",
    val goldPrice: Float = 0f,
    val millisLeft: Long = 0L,
    val validityInSeconds: Long = 0L,
    val isPriceDrop: Boolean = false
)
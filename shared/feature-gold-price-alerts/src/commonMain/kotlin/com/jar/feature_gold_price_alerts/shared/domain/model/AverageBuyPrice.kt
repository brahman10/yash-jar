package com.jar.feature_gold_price_alerts.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AverageBuyPrice(
    @SerialName("averageBuyPrice")
    val averageBuyPrice: Float
)

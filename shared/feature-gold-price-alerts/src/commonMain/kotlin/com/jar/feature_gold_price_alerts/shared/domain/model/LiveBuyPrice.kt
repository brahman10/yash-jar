package com.jar.feature_gold_price_alerts.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class LiveBuyPrice(
    @SerialName("applicableTax")
    val applicableTax: Double? = null,
    @SerialName("isPriceDrop")
    val isPriceDrop: Boolean? = null,
    @SerialName("price")
    val price: Double? = null,
    @SerialName("rateId")
    val rateId: String? = null,
    @SerialName("rateValidity")
    val rateValidity: String? = null,
    @SerialName("validity")
    val validity: Int? = null
)
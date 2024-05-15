package com.jar.app.feature_sell_gold.shared.domain.models

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UpdateReasonResponse(
    @SerialName("orderId")
    val orderId: String,

    @SerialName("reason")
    val reason: String
)
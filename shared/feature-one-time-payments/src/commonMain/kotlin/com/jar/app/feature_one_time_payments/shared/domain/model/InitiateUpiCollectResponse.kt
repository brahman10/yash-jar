package com.jar.app.feature_one_time_payments.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class InitiateUpiCollectResponse(
    @SerialName("orderId")
    val orderId: String,
)


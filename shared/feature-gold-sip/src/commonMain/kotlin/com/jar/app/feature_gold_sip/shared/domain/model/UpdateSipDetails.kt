package com.jar.app.feature_gold_sip.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UpdateSipDetails(
    @SerialName("subscriptionAmount")
    val subscriptionAmount: Float,
    @SerialName("subscriptionDay")
    val subscriptionDay: Int,
    @SerialName("subscriptionType")
    val subscriptionType: String
)

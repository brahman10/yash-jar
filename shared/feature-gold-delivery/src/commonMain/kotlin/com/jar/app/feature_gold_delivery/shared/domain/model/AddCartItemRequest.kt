package com.jar.app.feature_gold_delivery.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AddCartItemRequest(
    @SerialName("deliveryMakingCharge")
    val deliveryMakingCharge: Double? = null,
    @SerialName("productId")
    val productId: Int? = null,
    @SerialName("volume")
    val volume: Double? = null,
    @SerialName("label")
    val label: String? = null,
    @SerialName("quantity")
    val quantity: Int? = null
)
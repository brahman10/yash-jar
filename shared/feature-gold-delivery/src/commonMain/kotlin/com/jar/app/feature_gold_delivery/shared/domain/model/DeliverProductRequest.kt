package com.jar.app.feature_gold_delivery.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DeliverProductRequest(
    @SerialName("productCode")
    val productCode: Int,

    @SerialName("volume")
    val volume: Double,

    @SerialName("addressId")
    val addressId: String,

    @SerialName("deliveryMakingCharge")
    val deliveryMakingCharge: Double? = null,
)
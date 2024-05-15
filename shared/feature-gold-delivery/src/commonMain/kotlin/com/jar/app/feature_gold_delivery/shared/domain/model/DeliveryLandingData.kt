package com.jar.app.feature_gold_delivery.shared.domain.model

import com.jar.app.feature_transaction.shared.domain.model.TransactionData
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DeliveryLandingData(
    @SerialName("goldPartner")
    val goldPartner: List<GoldPartner?>? = null,
    @SerialName("landingImage")
    val landingImage: String? = null,
    @SerialName("previousOrder")
    val previousOrder: List<TransactionData>? = null,
    @SerialName("categories")
    val categories: List<String>? = null,
)

@kotlinx.serialization.Serializable
data class GoldPartner(
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("key")
    val key: String? = null,
    @SerialName("value")
    val value: Int? = null
)
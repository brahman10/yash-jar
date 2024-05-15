package com.jar.app.feature_gold_redemption.shared.data.network.model

import kotlinx.serialization.SerialName

data class OrderProcessingData(
    @SerialName("discountText")
    val discountText: String? = null,
)
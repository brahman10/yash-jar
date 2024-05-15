package com.jar.app.feature_one_time_payments.shared.domain.model.juspay

import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonElement

@kotlinx.serialization.Serializable
data class RecentlyUsedPaymentMethodData(
    @SerialName("paymentsData")
    val paymentsData: List<JsonElement>
)
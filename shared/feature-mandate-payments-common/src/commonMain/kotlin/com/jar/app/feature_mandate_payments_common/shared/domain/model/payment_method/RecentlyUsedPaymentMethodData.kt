package com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_method

import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonElement

@kotlinx.serialization.Serializable
data class RecentlyUsedPaymentMethodData(
    @SerialName("paymentsData")
    val paymentsData: List<JsonElement>
)
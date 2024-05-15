package com.jar.app.feature_one_time_payments.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class EnabledPaymentMethodResponse(
    @SerialName("paymentMethods")
    val paymentMethods: List<String>,
    @SerialName("whitelistedUpiApps")
    val whitelistedUpiApps: List<String>
)
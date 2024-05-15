package com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_method

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class EnabledPaymentMethodResponse(
    @SerialName("paymentMethods")
    val paymentMethods: List<String>,
    @SerialName("whitelistedUpiApps")
    val whitelistedUpiApps: List<String>
)
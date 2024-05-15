package com.jar.app.feature_one_time_payments.shared.domain.model.payment_method

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class PaymentMethodUpiIntent(
    @SerialName("paymentMethod")
    val paymentMethod: String,

    @SerialName("payerVpa")
    val payerVpa: String? = null,

    @SerialName("payerApp")
    val payerApp: String
): PaymentMethod()
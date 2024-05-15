package com.jar.app.feature_one_time_payments.shared.domain.model.payment_method

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class PaymentMethodNB(
    @SerialName("paymentMethod")
    val paymentMethod: String,

    @SerialName("paymentMethodType")
    val paymentMethodType: String
): PaymentMethod()
package com.jar.app.feature_one_time_payments.shared.domain.model.juspay

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
internal data class JuspayPaymentMethod(
    @SerialName("paymentMethodType")
    val paymentMethodType: String,

    @SerialName("paymentMethod")
    val paymentMethod: String,

    @SerialName("description")
    val description: String,
)
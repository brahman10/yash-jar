package com.jar.app.feature_one_time_payments.shared.domain.model.payment_method

import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.SavedCard
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class PaymentMethodCard(
    @SerialName("paymentMethod")
    val paymentMethod: String,

    @SerialName("cardFingerprint")
    val cardFingerprint: String,

    @SerialName("savedCard")
    var savedCard: SavedCard? = null
) : PaymentMethod()
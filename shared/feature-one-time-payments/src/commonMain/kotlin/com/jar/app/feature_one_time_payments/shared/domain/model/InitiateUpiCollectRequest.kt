package com.jar.app.feature_one_time_payments.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class InitiateUpiCollectRequest(
    @SerialName("orderId")
    val orderId: String,

    @SerialName("amount")
    val amount: Float,

    @SerialName("vpa")
    val vpa: String,

    @SerialName("paymentProvider")
    val paymentProvider: String,
) : Parcelable
package com.jar.app.feature_one_time_payments.shared.data.model.juspay

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class JuspayPaymentResponse(
    @SerialName("action")
    val action: String,

    @SerialName("status")
    val status: String? = null,

    @SerialName("orderId")
    val orderId: String,
) : Parcelable
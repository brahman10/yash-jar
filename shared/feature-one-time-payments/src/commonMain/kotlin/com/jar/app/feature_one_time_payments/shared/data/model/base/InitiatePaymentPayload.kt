package com.jar.app.feature_one_time_payments.shared.data.model.base

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class InitiatePaymentPayload(
    @SerialName("clientAuthToken")
    val clientAuthToken: String,

    @SerialName("id")
    val id: String,

    @SerialName("orderId")
    val orderId: String,

    @SerialName("callbackUrl")
    val callbackUrl: String
): Parcelable
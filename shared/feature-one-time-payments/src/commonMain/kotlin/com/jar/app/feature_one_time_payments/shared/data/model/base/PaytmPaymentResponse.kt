package com.jar.app.feature_one_time_payments.shared.data.model.base

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class PaytmPaymentResponse(

    @SerialName("orderId")
    val orderId: String,

    @SerialName("mid")
    val mid: String,

    @SerialName("subscriptionId")
    val subscriptionId: String? = null,

    @SerialName("txnAmount")
    val txnAmount: Float? = null,

    @SerialName("txnToken")
    val txnToken: String? = "",

    @SerialName("callbackUrl")
    val callbackUrl: String? = null
) : Parcelable
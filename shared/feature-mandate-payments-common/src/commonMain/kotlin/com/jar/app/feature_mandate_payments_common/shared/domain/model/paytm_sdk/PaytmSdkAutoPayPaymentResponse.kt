package com.jar.app.feature_mandate_payments_common.shared.domain.model.paytm_sdk

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class PaytmSdkAutoPayPaymentResponse(

    @SerialName("orderId")
    val orderId: String,

    @SerialName("mid")
    val mid: String,

    @SerialName("subscriptionId")
    val subscriptionId: String?,

    @SerialName("txnAmount")
    val txnAmount: Float,

    @SerialName("txnToken")
    val txnToken: String,

    @SerialName("callbackUrl")
    val callbackUrl: String
): Parcelable
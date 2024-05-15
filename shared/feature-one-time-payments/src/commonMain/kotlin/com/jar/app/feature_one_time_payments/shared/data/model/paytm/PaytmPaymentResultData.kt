package com.jar.app.feature_one_time_payments.shared.data.model.paytm

import kotlinx.serialization.SerialName
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize

@kotlinx.serialization.Serializable
@Parcelize
data class PaytmPaymentResultData(
    @SerialName("bankName")
    val bankName: String? = null,

    @SerialName("bankTxnId")
    val bankTxnId: String? = null,

    @SerialName("checksumHash")
    val checksumHash: String? = null,

    @SerialName("currency")
    val currency: String? = null,

    @SerialName("gatewayName")
    val gatewayName: String? = null,

    @SerialName("mid")
    val mid: String? = null,

    @SerialName("orderId")
    val orderId: String? = null,

    @SerialName("paymentMode")
    val paymentMode: String? = null,

    @SerialName("respCode")
    val respCode: String? = null,

    @SerialName("respMsg")
    val respMsg: String? = null,

    @SerialName("status")
    val status: String? = null,

    @SerialName("chargeAmount")
    val chargeAmount: String? = null,

    @SerialName("txnAmount")
    val txnAmount: String? = null,

    @SerialName("txnDate")
    val txnDate: String? = null,

    @SerialName("txnId")
    val txnId: String? = null,

    @SerialName("subsId")
    val subsId: String? = null,

    @SerialName("callbackUrl")
    val callbackUrl: String? = null,

    @SerialName("txnToken")
    val txnToken: String? = null,
) : Parcelable
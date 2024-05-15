package com.jar.app.feature_one_time_payments.shared.data.model.base

import com.jar.app.feature_one_time_payments.shared.data.model.juspay.JuspayPaymentResponse
import com.jar.app.feature_one_time_payments.shared.data.model.paytm.PaytmPaymentResultData
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class FetchManualPaymentRequest(

    @SerialName("paymentProvider")
    val paymentProvider: String,

    @SerialName("orderId")
    val orderId: String,

    @SerialName("paytm")
    val paytm: PaytmPaymentResultData? = null,

    @SerialName("juspay")
    val juspay: JuspayPaymentResponse? = null,

    @SerialName("transactionType")
    val transactionType: String? =null,

    @SerialName("flowContext")
    val flowContext:String? = null
) : Parcelable
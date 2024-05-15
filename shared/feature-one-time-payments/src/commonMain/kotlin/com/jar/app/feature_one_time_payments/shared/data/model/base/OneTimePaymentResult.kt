package com.jar.app.feature_one_time_payments.shared.data.model.base

import com.jar.app.core_base.domain.model.OneTimePaymentGateway
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_one_time_payments.shared.data.model.juspay.JuspayPaymentResponse
import com.jar.app.feature_one_time_payments.shared.data.model.paytm.InitiatePaytmPaymentResponse
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class OneTimePaymentResult(
    @SerialName("paymentGateway")
    val oneTimePaymentGateway: OneTimePaymentGateway,

    @SerialName("orderId")
    val orderId: String,

    @SerialName("amount")
    val amount: Float,

    @SerialName("fetchCurrentGoldPriceResponse")
    val fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse? = null,

    @SerialName("initiatePaytmPaymentResponse")
    val initiatePaytmPaymentResponse: InitiatePaytmPaymentResponse? = null,

    @SerialName("juspayPaymentResponse")
    val juspayPaymentResponse: JuspayPaymentResponse? = null,

    @SerialName("transactionType")
    val transactionType: String? = null,

    @SerialName("isRetryAllowed")
    val isRetryAllowed: Boolean? = null
) : Parcelable
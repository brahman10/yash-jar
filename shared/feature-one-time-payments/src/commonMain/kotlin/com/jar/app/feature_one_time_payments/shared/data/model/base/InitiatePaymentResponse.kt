package com.jar.app.feature_one_time_payments.shared.data.model.base

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import com.jar.app.core_base.domain.model.OneTimePaymentGateway
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class InitiatePaymentResponse(
    @SerialName("paymentProvider")
    private val paymentProvider: String,

    @SerialName("orderId")
    val orderId: String, // Only in /v2/api/delivery/placeOrder

    @SerialName("txnAmount")
    val amount: Float,

    @SerialName("priceResponse")
    val fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse? = null,

    @SerialName("paytm")
    val paytm: PaytmPaymentResponse? = null,

    @SerialName("jusPay")
    val juspay: InitiatePaymentPayload? = null,

    @SerialName("transactionType")
    val transactionType: String? = null,

    @SerialName("isRetryAllowed")
    var isRetryAllowed: Boolean? = null,

    var screenSource: String? = null
) : Parcelable {

    fun getPaymentProvider() = OneTimePaymentGateway.valueOf(paymentProvider)
}
package com.jar.app.feature_one_time_payments.shared.domain.model

import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class RetryPaymentRequest(
    @SerialName("orderId")
    val orderId: String,

    @SerialName("amount")
    val amount: Float,

    @SerialName("paymentProvider")
    val paymentProvider: String,

    @SerialName("priceResponse")
    val priceResponse: FetchCurrentGoldPriceResponse,
)
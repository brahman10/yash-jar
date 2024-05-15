package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class InitiatePaymentRequest(
    @SerialName("txnAmt")
    val txnAmt: Float,

    @SerialName("orderId")
    val orderId: String,

    @SerialName("paymentProvider")
    val paymentProvider: String,

    @SerialName("loanTxnCategory")
    val loanTxnCategory: String,

    @SerialName("transactionType")
    val transactionType: String,

    @SerialName("emiType")
    val emiType: String? = null
)

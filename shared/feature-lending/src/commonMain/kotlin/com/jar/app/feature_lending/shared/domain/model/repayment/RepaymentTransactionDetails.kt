package com.jar.app.feature_lending.shared.domain.model.repayment


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepaymentTransactionDetails(
    @SerialName("emiAmount")
    val emiAmount: String? = null,
    @SerialName("paymentBreakdown")
    val paymentBreakdown: List<PaymentBreakDownDetails>? = null,
    @SerialName("paymentBreakdownHeader")
    val paymentBreakdownHeader: String? = null,
    @SerialName("paymentDate")
    val paymentDate: String? = null,
    @SerialName("paymentMode")
    val paymentMode: String? = null,
    @SerialName("transactionHeader")
    val transactionHeader: String? = null,
    @SerialName("transactionId")
    val transactionId: String? = null,
    @SerialName("transactionStatus")
    val transactionStatus: String? = null,
    @SerialName("transactionStatusText")
    val transactionStatusText: String? = null,
    @SerialName("emiLottie")
    val emiLottie: String? = null,
    @SerialName("paymentStatusText")
    val paymentStatusText: String? = null,
)
package com.jar.app.feature_lending.shared.domain.model.repayment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmiTxnCommonData(
    @SerialName("emiTitle")
    val emiTitle: String? = null,
    @SerialName("emiDate")
    val emiDate: String? = null,
    @SerialName("emiAmount")
    val emiAmount: String? = null,
    @SerialName("paymentStatusText")
    val paymentStatusText: String? = null,
    @SerialName("paymentStatus")
    val paymentStatus: String? = null,
    @SerialName("transactionStatus")
    val transactionStatus: String? = null,
    @SerialName("paymentId")
    val paymentId: String? = null
) {
    fun getPaymentStatus(): RepaymentStatus {
        return try {
            RepaymentStatus.valueOf(paymentStatus.orEmpty())
        } catch (e: Exception) {
            RepaymentStatus.UPCOMING
        }
    }

    fun getTransactionStatus(): RepaymentTxnStatus {
        return try {
            RepaymentTxnStatus.valueOf(transactionStatus.orEmpty())
        } catch (e: Exception) {
            RepaymentTxnStatus.FAILURE
        }
    }
}
package com.jar.app.feature_transaction.shared.domain.model

import com.jar.app.feature_transactions_common.shared.CommonTransactionStatus
import com.jar.app.feature_transactions_common.shared.TransactionCategory
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.ColorResource
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class Transaction(
    @SerialName("amount")
    val amount: String? = null,

    @SerialName("initialInvestment")
    val initialInvestment: Boolean? = null,

    @SerialName("investmentMode")
    val investmentMode: String? = null,

    @SerialName("investmentStatus")
    private val investmentStatus: String? = null,

    @SerialName("status")
    private val status: String? = null,

    @SerialName("roundOffAmount")
    val roundOffAmount: Double? = null,

    @SerialName("timestamp")
    val timestamp: String,

    @SerialName("title")
    val title: String,

    @SerialName("txnCategory")
    val txnCategory: String? = null,

    @SerialName("txnDate")
    val txnDate: String? = null,

    @SerialName("txnId")
    val txnId: String? = null,
) : Parcelable {

    fun getCustomStatus() = status ?: investmentStatus

    fun getAmountToShow(): String? {
        return if (txnCategory == TransactionCategory.DAILY_SAVINGS.name || initialInvestment == true)
            roundOffAmount?.toString()
        else
            amount
    }

    fun shouldShowRoundOff(): Boolean {
        return txnCategory != TransactionCategory.DAILY_SAVINGS.name
                && initialInvestment != true
                && roundOffAmount != null
    }

    fun getColorForStatus(): ColorResource {
        val transactionStatus = getCustomStatus()?.uppercase()
        val status: CommonTransactionStatus =
            CommonTransactionStatus.values().find { it.name == transactionStatus }
                ?: CommonTransactionStatus.DEFAULT
        return status.getColor()
    }
}
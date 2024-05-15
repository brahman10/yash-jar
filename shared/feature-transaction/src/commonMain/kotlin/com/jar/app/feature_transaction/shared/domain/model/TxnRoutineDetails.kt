package com.jar.app.feature_transaction.shared.domain.model

import com.jar.app.core_base.util.BaseConstants
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TxnRoutineDetails(
    @SerialName("invoiceAvailable")
    val invoiceAvailable: Boolean? = null,
    @SerialName("invoiceLink")
    val invoiceLink: String? = null,
    @SerialName("refreshAllowed")
    val refreshAllowed: Boolean? = null,
    @SerialName("retryAllowed")
    val retryAllowed: Boolean? = null,
    @SerialName("showGiftReminder")
    val showGiftingReminder: Boolean? = null,
    @SerialName("retryButtonTxt")
    val retryButtonTxt: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("txnRoutine")
    val txnRoutine: List<TxnRoutine>? = null,
    @SerialName("txnType")
    val txnType: String? = null,
    @SerialName("failureReason")
    val failureReason: String? = null,

    override val uniqueKey: String = txnType?.plus(txnRoutine?.size)?.plus(failureReason)
        ?.plus(status)?.plus(retryButtonTxt)?.plus(invoiceLink).orEmpty()

) : TxnDetailsCardView {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun getSortKey(): Int {
        return BaseConstants.TxnDetailsPosition.TRANSACTION_ROUTINE
    }

    override fun equals(other: Any?): Boolean {
        return other is TxnRoutine
    }
}
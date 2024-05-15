package com.jar.app.feature_transaction.shared.domain.model.new_transaction_details

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_transactions_common.shared.NewTransactionRoutine
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TransactionStatusCard(
    @SerialName("title")
    val title: String? = null,

    @SerialName("invoiceLink")
    val invoiceLink: String? = null,

    @SerialName("txnRoutineList")
    val txnRoutineList: List<NewTransactionRoutine>? = null
) : NewTransactionDetailsCardView {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun getSortKey(): Int {
        return BaseConstants.NewTransactionDetailsPosition.TRANSACTION_STATUS
    }

    override fun equals(other: Any?): Boolean {
        return other is TransactionStatusCard
    }
}
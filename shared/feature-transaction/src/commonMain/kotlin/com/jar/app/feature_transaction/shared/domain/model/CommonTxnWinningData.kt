package com.jar.app.feature_transaction.shared.domain.model

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_transactions_common.shared.CommonTransactionValueType
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize

@Parcelize
@kotlinx.serialization.Serializable
data class CommonTxnWinningData(
    val title: String?,
    val subtitle: String?,
    val iconLink: String?,
    val amount: Float?,
    val date: String?,
    val status: String?,
    val statusInfo: StatusInfo?,
    val sourceType: String?,
    val txnId: String?,
    val orderId: String?,
    val valueType: String?,
    val volume: Float?,
    override val uniqueKey: String = title?.plus(subtitle)?.plus(amount)?.plus(status)?.plus(volume)
        ?.plus(txnId)?.plus(orderId).orEmpty()

) : Parcelable, TxnDetailsCardView {
    fun getValueType() = CommonTransactionValueType.values().find { it.name == valueType }
        ?: CommonTransactionValueType.AMOUNT

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun getSortKey(): Int {
        return BaseConstants.TxnDetailsPosition.TRANSACTION_STATUS
    }

    override fun equals(other: Any?): Boolean {
        return other is TransactionData
    }
}
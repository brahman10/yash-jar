package com.jar.app.feature_transaction.shared.domain.model

import com.jar.app.core_base.util.BaseConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PauseTxnDetails(
    @SerialName("title")
    val title: String?= null,

    @SerialName("description")
    val description: String?= null,

    override val uniqueKey: String = title?.plus(description).orEmpty()

) : TxnDetailsCardView {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun getSortKey(): Int {
        return BaseConstants.TxnDetailsPosition.PAUSE_TXN_DETAILS
    }

    override fun equals(other: Any?): Boolean {
        return other is SavingTxnDetails
    }
}
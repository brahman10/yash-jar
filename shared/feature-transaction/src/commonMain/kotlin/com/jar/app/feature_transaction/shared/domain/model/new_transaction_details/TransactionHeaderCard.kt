package com.jar.app.feature_transaction.shared.domain.model.new_transaction_details

import com.jar.app.core_base.util.BaseConstants
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TransactionHeaderCard(
    @SerialName("title")
    val title: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("icon")
    val icon: String? =null,

    @SerialName("value")
    val value: String? = null,

    @SerialName("subtitleComponentText")
    val subtitleComponentText: String? = null,

    @SerialName("subtitleComponentIcon")
    val subtitleComponentIcon: String? = null
) : NewTransactionDetailsCardView {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun getSortKey(): Int {
        return BaseConstants.NewTransactionDetailsPosition.TRANSACTION_HEADER
    }

    override fun equals(other: Any?): Boolean {
        return other is TransactionHeaderCard
    }
}

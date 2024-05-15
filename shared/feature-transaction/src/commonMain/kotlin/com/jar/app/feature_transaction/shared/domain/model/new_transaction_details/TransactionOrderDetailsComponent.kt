package com.jar.app.feature_transaction.shared.domain.model.new_transaction_details

import com.jar.app.core_base.util.BaseConstants
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TransactionOrderDetailsComponent(
    @SerialName("title")
    val title: String? = null,

    @SerialName("orderDetailsCardList")
    val orderDetailsCardList: List<OrderDetailsCardList>? = null
) : NewTransactionDetailsCardView {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun getSortKey(): Int {
        return BaseConstants.NewTransactionDetailsPosition.TRANSACTION_ORDER_DETAILS
    }

    override fun equals(other: Any?): Boolean {
        return other is TransactionOrderDetailsComponent
    }
}

@kotlinx.serialization.Serializable
data class OrderDetailsCardList(
    @SerialName("title")
    val title: String? = null,

    @SerialName("orderDetailsCardRowList")
    val orderDetailsCardRowList: List<NewTransactionTitleValuePair>? = null
)
package com.jar.app.feature_gold_delivery.shared.ui.store_item.list
import com.jar.app.feature_transaction.shared.domain.model.TransactionData

sealed class MyOrdersData {
    data class MyOrdersHeader(
        val title: String
    ) : MyOrdersData()

    data class MyOrdersBody(
        val body: TransactionData
    ) : MyOrdersData()
}
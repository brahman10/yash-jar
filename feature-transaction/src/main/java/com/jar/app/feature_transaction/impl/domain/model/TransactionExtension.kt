package com.jar.app.feature_transaction.impl.domain.model

import androidx.annotation.DrawableRes
import com.jar.app.feature_transaction_common.getIcon
import com.jar.app.feature_transactions_common.shared.TransactionCategory

@DrawableRes
fun com.jar.app.feature_transaction.shared.domain.model.Transaction.getIconForCategory(): Int {
    val category = txnCategory?.uppercase()
    val status: TransactionCategory =
        TransactionCategory.values().find { it.name == category }
            ?: TransactionCategory.DEFAULT
    return status.getIcon()
}
package com.jar.app.event

import com.jar.app.feature_transaction.shared.domain.model.TransactionType

data class RedirectToTransactionEvent(
    val transactionType: TransactionType
)
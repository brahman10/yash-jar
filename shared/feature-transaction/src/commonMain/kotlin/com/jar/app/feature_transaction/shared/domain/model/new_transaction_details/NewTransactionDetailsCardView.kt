package com.jar.app.feature_transaction.shared.domain.model.new_transaction_details

interface NewTransactionDetailsCardView {
    fun getSortKey(): Int

    override fun equals(other: Any?): Boolean
}
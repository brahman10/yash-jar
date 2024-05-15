package com.jar.app.feature_transaction.shared.domain.model

interface TxnDetailsCardView {

    val uniqueKey: String

    fun getSortKey(): Int

    override fun equals(other: Any?): Boolean
}
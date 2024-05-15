package com.jar.app.feature_transaction.shared.domain.model.new_transaction_details

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class NewTransactionTitleValuePair(
    @SerialName("title")
    val title: String? = null,

    @SerialName("value")
    val value: String? = null,

    @SerialName("rowCosmetics")
    private val rowCosmetics: String? = null
) {
    fun getRowCosmetics() = OrderDetailsCardRowCosmetics.values().find { it.name == rowCosmetics }

    fun maskTransactionId(transactionId: String): String {
        val masked = StringBuilder()
        masked.append(".......")
        masked.append(transactionId.takeLast(6).orEmpty())
        return masked.toString()
    }
}

enum class OrderDetailsCardRowCosmetics {
    TXN_ID,
    HIGHLIGHTED,
    WEBSITE
}
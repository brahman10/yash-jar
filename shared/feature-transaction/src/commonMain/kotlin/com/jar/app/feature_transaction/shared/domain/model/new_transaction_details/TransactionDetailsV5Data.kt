package com.jar.app.feature_transaction.shared.domain.model.new_transaction_details

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TransactionDetailsV5Data(
    @SerialName("transactionHeaderCard")
    val transactionHeaderCard: TransactionHeaderCard? = null,

    @SerialName("transactionStatusCard")
    val transactionStatusCard: TransactionStatusCard? = null,

    @SerialName("transactionOrderDetailsComponent")
    val transactionOrderDetailsComponent: TransactionOrderDetailsComponent? = null,

    @SerialName("toolbarTitle")
    val toolbarTitle: String? = null,

    @SerialName("whatsappMessage")
    val whatsappMessage: String? = null
)
package com.jar.app.feature_transaction.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TxnDetails(
    @SerialName("title")
    val title: String? = null,
    @SerialName("value")
    val value: String? = null
)
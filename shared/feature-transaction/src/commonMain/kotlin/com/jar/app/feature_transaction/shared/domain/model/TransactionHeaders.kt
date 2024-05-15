package com.jar.app.feature_transaction.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TransactionHeaders(
    @SerialName("amount")
    val amount: Float? = null,
    @SerialName("assetTransactionId")
    val assetTransactionId: String? = null,
    @SerialName("currentStatus")
    val currentStatus: String? = null,
    @SerialName("date")
    val date: String? = null,
    @SerialName("iconLink")
    val iconLink: String? = null,
    @SerialName("orderId")
    val orderId: String? = null,
    @SerialName("sourceType")
    val sourceType: String? = null,
    @SerialName("statusEnum")
    val statusEnum: String? = null,
    @SerialName("statusInfo")
    val statusInfo: StatusInfo? = null,
    @SerialName("subTitle")
    val subTitle: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("volume")
    val volume: Float? = null,
    @SerialName("valueType")
    val valueType: String? = null,


)
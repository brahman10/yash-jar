package com.jar.app.feature_user_api.data.dto

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class FullPaymentInfoDTO(
    @SerialName("txnAmt")
    val txnAmt: Float,

    @SerialName("title")
    var title: String,

    @SerialName("orderId")
    val orderId: String,

    @SerialName("description")
    val description: String? = null,

    @SerialName("nudgeText")
    val nudgeText: String? = null,
)
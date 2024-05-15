package com.jar.app.feature_user_api.domain.model

@kotlinx.serialization.Serializable
data class FullPaymentInfo(
    val txnAmt: Float,

    var title: String,

    val orderId: String,

    val description: String?,

    val nudgeText: String?,
)
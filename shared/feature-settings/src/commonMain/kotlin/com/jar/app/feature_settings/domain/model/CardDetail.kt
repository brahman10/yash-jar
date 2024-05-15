package com.jar.app.feature_settings.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class CardDetail(
    @SerialName("cardNo")
    val cardNumber: String,
    @SerialName("nameOnCard")
    val nameOnCard: String,
    @SerialName("expYear")
    val cardExpYear: String,
    @SerialName("expMonth")
    val cardExpMonth: String,
    @SerialName("customer_email")
    val customerEmail: String? = null,
    @SerialName("nickname")
    val nickname: String? = null
)

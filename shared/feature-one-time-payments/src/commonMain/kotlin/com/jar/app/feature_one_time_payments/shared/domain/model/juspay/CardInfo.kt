package com.jar.app.feature_one_time_payments.shared.domain.model.juspay

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class CardInfo(
    @SerialName("type")
    val type: String,

    @SerialName("brand")
    val brand: String,

    @SerialName("bank")
    val bank: String? = null
)
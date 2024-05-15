package com.jar.app.feature_user_api.domain.model

@kotlinx.serialization.Serializable
data class PartPaymentOption(
    val percentage: Int?,

    val amount: Float?
)
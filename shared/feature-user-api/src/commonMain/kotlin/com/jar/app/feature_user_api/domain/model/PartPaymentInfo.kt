package com.jar.app.feature_user_api.domain.model


@kotlinx.serialization.Serializable
data class PartPaymentInfo(
    val title: String,

    val description: String?,

    val skipAvailable: Boolean,

    val skipInfo: String?,

    val paymentOptions: List<PartPaymentOption>
)
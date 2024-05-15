package com.jar.app.feature_user_api.data.dto

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class PartPaymentInfoDTO(
    @SerialName("title")
    val title: String,

    @SerialName("description")
    val description: String? = null,

    @SerialName("skipAvailable")
    val skipAvailable: Boolean,

    @SerialName("skipInfo")
    val skipInfo: String? = null,

    @SerialName("paymentOptions")
    val paymentOptions: List<PartPaymentOptionDTO>
)
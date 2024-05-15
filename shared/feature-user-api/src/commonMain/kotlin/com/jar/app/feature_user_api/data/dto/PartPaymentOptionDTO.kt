package com.jar.app.feature_user_api.data.dto

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class PartPaymentOptionDTO(
    @SerialName("percentage")
    val percentage: Int? = null,

    @SerialName("amount")
    val amount: Float? = null
)
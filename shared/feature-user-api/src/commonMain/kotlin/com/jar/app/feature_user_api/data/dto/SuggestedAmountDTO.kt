package com.jar.app.feature_user_api.data.dto

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SuggestedAmountDTO(
    @SerialName("amount")
    val amount: Float,

    @SerialName("recommended")
    val recommended: Boolean,

    @SerialName("unit")
    val unit: String? = null,
)
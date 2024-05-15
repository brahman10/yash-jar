package com.jar.app.feature_goal_based_saving.shared.data.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class CalculateDailyAmountResponse(
    @SerialName("header") val header: String? = null,
    @SerialName("amount") val amount: Int? = null,
    @SerialName("subText") val subText: String? = null,
    @SerialName("footerButtonText") val footerButtonText: String? = null
)
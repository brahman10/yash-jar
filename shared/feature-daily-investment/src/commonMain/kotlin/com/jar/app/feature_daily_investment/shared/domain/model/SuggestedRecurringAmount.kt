package com.jar.app.feature_daily_investment.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SuggestedRecurringAmount(
    @SerialName("amount")
    val amount: Float,

    @SerialName("recommended")
    val recommended: Boolean

)
package com.jar.app.feature_daily_investment.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DailyInvestmentStatus(
    @SerialName("amount")
    val amount: Float,

    @SerialName("enabled")
    val enabled: Boolean,

    @SerialName("savingsPaused")
    val savingsPaused: Boolean,

    @SerialName("updatedAt")
    val updatedAt: Long? = null
)
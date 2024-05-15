package com.jar.app.feature_daily_investment.impl.domain.model

@kotlinx.serialization.Serializable
data class DailyInvestmentProjectionBreakdownData(
    val dailyInvestment: Float,
    val currentGoldPrice: Float,
    val totalGoldAfterNDays: Double,
    val totalDays: Int,
    val gst: Float
)
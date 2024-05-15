package com.jar.app.feature_daily_investment.shared.domain.model

data class DailySavingsUpdateFlowValues (
    val currentDailySavingsAmount: Float?,
    val recommendedDailySavingsAmount: Float?,
    val currentDailySavingsProjection: Float?,
    val recommendedDailySavingsProjection: Float?,
)
package com.jar.app.feature_daily_investment.api.domain.event

data class SetupMandateEvent(
    val newMandateAmount: Float,
    val newDailySavingAmount: Float? = null
)
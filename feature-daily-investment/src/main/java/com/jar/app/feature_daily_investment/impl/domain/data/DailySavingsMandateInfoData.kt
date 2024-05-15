package com.jar.app.feature_daily_investment.impl.domain.data

data class DailySavingsMandateInfoData(
    val mandateType: String,
    val value: String,
    val status: String?,
    val isCurrentMandate: Boolean
)
package com.jar.app.feature_daily_investment.impl.data

import kotlinx.serialization.Serializable


@Serializable
data class DailySavingSetupStatusData(
    val dailySavingAmount: Float,
    val isFromOnboarding: Boolean,
    val flowName: String,
    val isMandateBottomSheetFlow: Boolean?,
    val userLifecycle: String?
)

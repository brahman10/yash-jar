package com.jar.app.feature_daily_investment.api.domain.event

data class SetupAutoPayEvent(
    val oldDailySavingAmount: Int? = null,
    val newDailySavingAmount: Float? = null,
    val mandateAmount: Float? = null,
    val shouldInvokePGDirectly: Boolean = false,
    val shouldDirectlyShowAppSelectionScreen: Boolean = false,
    val isDailySavingAutoPayFlow: Boolean = false,
    val isRoundOffAutoPayFlow: Boolean = false,
    val authWorkFlowType: String? = null,
    val flowName: String? = null,
    val bestAmount: String? = null,
    val userLifecycle: String? = null
)

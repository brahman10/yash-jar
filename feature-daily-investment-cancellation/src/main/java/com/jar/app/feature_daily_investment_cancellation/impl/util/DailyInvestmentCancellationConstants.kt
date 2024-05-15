package com.jar.app.feature_daily_investment_cancellation.impl.util

object DailyInvestmentCancellationConstants {
    internal object Endpoints {
        const val FIRST_SETUP = "/v2/dailySavings/user/firstSetup"
        const val DAILY_SAVING_TEMPERING = "/v2/dailySavings/user/settings"
        const val DAILY_SAVING_REDIRECTION = "/v2/user/settings/redirection"
    }

    const val PENDING = "PENDING"
    const val FAILURE = "FAILURE"
}

enum class DailyInvestmentCancellationEnum {
    POST_CANCELLATION,
    ACTIVE,
    STOPPED,
    PAUSED,
    DISABLED,
    V1,
    V2,
    V3,
    V4
}
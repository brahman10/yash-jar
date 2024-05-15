package com.jar.app.feature_daily_investment_cancellation.shared.util

object DailyInvestmentCancellationConstants {

    internal object Endpoints {
        const val FETCH_DAILY_SAVINGS_SETTING_DATA = "v2/user/dailySavings/settings"
        const val FETCH_DAILY_SAVINGS_PAUSE_DETAILS = "v2/user/dailySavings/pauseDetails"
        const val FETCH_DAILY_SAVINGS_CONFIRMATION_DETAILS = "v2/user/dailySavings/confirmActionDetails"
        const val FETCH_DAILY_SAVINGS_POST_CANCELLATION = "v2/user/dailySavings/postCancellation"
    }

    const val STATISTICS = "STATISTICS"
    const val KNOWLEDGE = "KNOWLEDGE"
}
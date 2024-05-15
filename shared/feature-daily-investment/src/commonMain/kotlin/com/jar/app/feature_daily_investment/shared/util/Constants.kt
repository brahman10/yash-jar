package com.jar.app.feature_daily_investment.shared.util

internal object Constants {

    internal object Endpoints {
        const val FETCH_DAILY_INVESTMENT_STATUS = "v1/api/user/dailySavings"
        const val UPDATE_DAILY_INVESTMENT_STATUS = "v2/api/dashboard/recurring"
        const val FETCH_DAILY_INVESTMENT_OPTIONS = "v2/api/dashboard/static"
        const val ARE_SAVINGS_PAUSED = "v2/api/user/are_savings_paused"
        const val UPDATE_PAUSE_SAVING_DURATION = "v2/api/user/savings"
        const val FETCH_DAILY_SAVING_EDUCATION = "v2/api/dashboard/static"
        const val FETCH_ABANDON_DAILY_INVESTMENT_DATA = "v2/api/dashboard/static"
        const val FETCH_AMOUNT_SELECTION_DATA = "v2/api/dashboard/static"
        const val FETCH_DAILY_INVESTMENT_FAQ = "v2/api/dashboard/static"
        const val FETCH_AUTOPAY_LANDING_DATA = "v1/api/engagement/autopay/landing"
        const val FETCH_DAILY_INVESTMENT_INTRO_BOTTOM_SHEET_DATA = "v2/api/dashboard/static"
        const val FETCH_DAILY_INVESTMENT_MANDATE_DATA = "v1/api/postSetup/mandateScreenDetails"
        const val FETCH_DAILY_INVESTMENT_STORIES = "v2/user/dailySavings/screenContent"
        const val FETCH_DAILY_INVESTMENT_ONBOARDING_FRAGMENT_DATA = "v1/api/user/settings/ds/content"
        const val FETCH_DAILY_INVESTMENT_BOTTOM_SHEET_V2 = "v2/api/dashboard/static"
        const val FETCH_UPDATE_DAILY_INVESTMENT_STATIC_DATA = "v1/api/features/updateJourney"
    }
}
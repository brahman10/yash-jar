package com.jar.app.feature_savings_common.shared.util

internal object SavingsConstants {

    internal object Endpoints {
        const val FETCH_SAVING_DETAILS = "v1/api/user/settings/savingsDetails"
        const val DISABLE_SAVINGS = "v1/api/user/settings/disableSavings"
        const val FETCH_SAVING_SETUP_INFO = "v1/api/user/settings/getSavingsSetupInfo"
        const val UPDATE_SAVING_DETAILS = "v1/api/user/settings/updateSavingsDetails"
        const val MANAGE_SAVING_PREFERENCE = "v1/api/user/settings/manageSavingsPreference"
        const val GOAL_BASED_SAVING_SETTINGS = "v2/api/user/fetchDailySavingsSettings"
    }
}
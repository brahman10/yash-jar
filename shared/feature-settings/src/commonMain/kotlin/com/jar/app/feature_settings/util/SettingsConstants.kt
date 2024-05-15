package com.jar.app.feature_settings.util

object SettingsConstants {

    object PaymentMethodsPosition {
        const val UPI = 0
        const val CARDS = 1
    }

    internal object Endpoints {
        const val FETCH_SUPPORTED_LANGUAGES = "api/preferences/languages"
        const val FETCH_ARE_SAVINGS_PAUSED = "v2/api/user/are_savings_paused"
        const val UPDATE_USER_SAVING_DURATION = "v2/api/user/savings"
        const val FETCH_VPA_CHIPS = "v2/api/payouts/vpa/chips"
        const val VERIFY_VPA = "v2/api/payments/verify-vpa"
        const val ADD_NEW_VPA = "v2/api/user/vpa/add/vpa"
        const val FETCH_CARD_BIN_INFO = "v1/api/paymentMethods/cardBinInfo"
        const val FETCH_USER_SAVED_CARDS = "v1/api/paymentMethods/listSavedCards"
        const val ADD_NEW_CARD = "v1/api/paymentMethods/addCard"
        const val DELETE_SAVED_CARD = "v1/api/paymentMethods/deleteSavedCard"
        const val DAILY_SAVING_REDIRECTION = "/v2/user/settings/redirection"
    }

    object AnalyticsKey {
        const val Language = "App_Language"
    }
}
package com.jar.app.feature.home.util

object HomeConstants {

    internal object Endpoints {
        const val UPDATE_SESSION = "v2/api/session"
        const val FETCH_DASHBOARD_STATIC_CONTENT = "v2/api/dashboard/static"
        const val FETCH_PUBLIC_STATIC_CONTENT = "/v2/api/public/static"
        const val FETCH_NOTIFICATION_LIST = "v1/api/notification"
        const val FETCH_INVOICE_LIST = "v1/api/user/gold/invoices"
        const val FETCH_SURVEY = "v1/api/user/survey"
        const val SUBMIT_SURVEY = "v1/api/user/survey/response"
        const val FETCH_DOWNTIME = "downtime"
        const val FETCH_PROMO_CODE_LIST = "v1/api/promoCode"
        const val FETCH_ACTIVE_ANALYTICS_LIST = "api/preferences/activeAnalytics"
        const val UPDATE_AD_DATA_SOURCE = "v1/api/userData/adSource"
        const val CAPTURE_APP_OPENS = "v1/api/dashboard/appOpens"
        const val FETCH_FORCE_UPDATE_DATA = "v1/api/dashboard/forceUpdate"
        const val FETCH_IS_KYC_REQUIRED = "v1/api/payoutCheck/kycRequired"
    }

    object AnalyticsKeys {
        const val fromScreen = "fromScreen"
        const val name = "name"
        const val newNotification = "newNotification"
        const val yes = "yes"
        const val no = "no"
        const val Home = "Home"
        const val Tile = "Tile"
        const val Profile = "Profile"
        const val timeSpent = "timeSpent"
        const val Transactions = "Transactions"
        const val Account = "Account"
        const val tileFunction = "tileFunction"
        const val position = "Position"
        const val Type = "Type"
        const val Winnings_Status = "Winnings_Status"
        const val Clicked_HamburgerIcon = "Clicked_HamburgerIcon"
        const val Shown_HamburgerMenu = "Shown_HamburgerMenu"
        const val Exit_HamburgerMenu = "Exit_HamburgerMenu"
        const val Clicked_ViewProfile_HamburgerMenu = "Clicked_ViewProfile_HamburgerMenu"
        const val Clicked_Avatar_HamburgerMenu = "Clicked_Avatar_HamburgerMenu"
        const val Clicked_Tile_HamburgerMenu = "Clicked_Tile_HamburgerMenu"
        const val Clicked_Logout_HamburgerMenu = "Clicked_Logout_HamburgerMenu"
        const val Clicked_UpdateAvailable = "Clicked_UpdateAvailable"
    }
}
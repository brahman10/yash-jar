package com.jar.feature_gold_price_alerts.shared.util

object GoldPriceAlertsConstants {
    internal object Endpoints {
        const val FETCH_GOLD_PRICE_TREND = "v1/api/goldPrices/trends"
        const val FETCH_GOLD_PRICE_SCREEN_STATIC = "v1/api/goldPrices/pageDetails"
        const val FETCH_GOLD_PRICE_BOTTOMSHEET_STATIC = "v1/api/goldPrices/alert/creationPage"
        const val FETCH_AVERAGE_BUY_PRICE = "v1/api/dashboard/goldPrices/averageBuyPrice"
        const val POST_NEW_ALERT = "v1/api/goldPrices/alert/create"
        const val FETCH_LATEST_ALERT = "v1/api/goldPrices/alert/getLatest"
        const val DISABLE_LATEST_ALERT = "v1/api/goldPrices/alert/disable"
        const val FETCH_GOLD_PRICE_TREND_HOMESCREEN_TAB = "v1/api/goldPrices/homeScreenPill"
    }

    object Constants {
        const val UPDATE_STATE_FROM_BOTTOMSHEET = "UPDATE_STATE_FROM_BOTTOMSHEET"
    }

    object AnalyticKeys {
        const val GoldPrice_HomeScreenShown = "GoldPrice_HomeScreenShown"
        const val GoldPrice_HomeScreenClicked = "GoldPrice_HomeScreenClicked"
        const val GoldPrice_AlertBSShown = "GoldPrice_AlertBSShown"
        const val GoldPrice_AlertBSClicked = "GoldPrice_AlertBSClicked"

        const val Set_Price_Alert = "Set Price Alert"
        const val Cross_clicked = "Cross clicked"
        const val Save_now = "Save_now"
        const val Nudge = "Nudge"
        const val pricedurationclicked ="pricedurationclicked"
        const val alertpriceshown ="alertpriceshown"
        const val alertpricechosen ="alertpricechosen"
        const val graphclicked ="graphclicked"

        const val cardtitle = "cardtitle" // {Start small Save big, Best time is to save now}
        const val carddescription = "carddescription" // {Begin savings with low as 10, Continue your savings journey with low as 10}
        const val messageshown = "messageshown" // {Your average price for this period  is Rs.5518.25/gm, You have saved 2.34 gm gold in this period,……}
        const val clickaction = "clickaction" // {Set Alert, Save Now}
        const val timespent = "timespent" // {5 secs, 10 secs, 15 secs,…….}

        const val title = "title"
        const val flowContext = "flowContext"
        const val cardFlowType = "cardFlowType"
        const val ActiveAlertsExists = "activeAlertExists"
        const val alertstatus = "alertstatus"
        const val Lowest_Price_Nudge = "Lowest_Price_Nudge"
        const val PopularAlertPrice = "PopularAlertPrice"

        const val Remove_Alert = "Remove Alert"
        const val Cross_Clicked = "Cross Clicked"
    }
}
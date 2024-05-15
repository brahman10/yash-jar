package com.jar.app.feature_spends_tracker.shared.utils

internal object SpendsTrackerConstants {

    internal object Endpoints {
        const val FETCH_SPENDS_METADATA = "v1/api/spendsTracker/fetch"
        const val FETCH_SPENDS_LIST = "v1/api/spendsTracker/transactions/list"
        const val FETCH_SPENDS_EDUCATION_DATA = "v1/api/spendsTracker/educationScreen"
        const val REPORT_TRANSACTION = "v1/api/spendsTracker/reportTransaction"
    }
}
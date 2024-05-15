package com.jar.app.core_analytics.di

import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi

class AnalyticsModule(shouldEnableLogging: Boolean) {

    companion object {
        private const val ANALYTICS_LOG_TAG = "#ANALYTICS_LOG_TAG#"
    }

    val analyticsApi by lazy {
        AnalyticsApi.getInstance(
            logTag = ANALYTICS_LOG_TAG,
            enableEventLoggingInLogcat = shouldEnableLogging
        )
    }

}
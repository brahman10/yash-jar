package com.jar.app.core_analytics

import android.content.Context
import com.appsflyer.AppsFlyerLib
import com.jar.internal.library.jarcoreanalytics.api.IAppAnalyticsService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppsFlyerAnalyticsService @Inject constructor(
    private val appsFlyerLib: AppsFlyerLib,
    @ApplicationContext private val context: Context
) : IAppAnalyticsService {

    override fun init() {
        appsFlyerLib.start(context)
        appsFlyerLib.init(CoreAnalyticsBuildKonfig.APPSFLYER_DEV_KEY, null, context)
    }

    override suspend fun onUpdateFcmToken(fcmToken: String) {
        appsFlyerLib.updateServerUninstallToken(context, fcmToken)
    }

    override suspend fun onUserLogin(id: String, values: Map<String, Any>) {
        appsFlyerLib.setCustomerUserId(id)
    }

    override suspend fun postEvent(eventName: String) {
        appsFlyerLib.logEvent(context, eventName, null)
    }

    override suspend fun postEvent(eventName: String, value: String) {
        appsFlyerLib.logEvent(context, eventName, mapOf("value" to value))
    }

    override suspend fun postEvent(eventName: String, values: Map<String, Any>) {
        appsFlyerLib.logEvent(context, eventName, values)
    }

    override suspend fun sendPurchaseEvent(value: Float) {

    }

    override suspend fun setAttributionData(attributionData: Map<String, String>) {

    }

    override suspend fun setUserLogout() {

    }

    override suspend fun setUserProperties(properties: List<Pair<String, String>>) {
        properties.forEach { (key, value) ->
            if (key == "id") {
                appsFlyerLib.setCustomerUserId(value)
            }
        }
    }
}
package com.jar.app.core_analytics

import android.content.Context
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.pushnotification.fcm.CTFcmMessageHandler
import com.jar.internal.library.jarcoreanalytics.api.IAppAnalyticsService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClevertapAnalyticsService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cleverTapAPI: CleverTapAPI
) : IAppAnalyticsService {

    override fun init() {
        cleverTapAPI.enableDeviceNetworkInfoReporting(true)
    }

    override suspend fun onUpdateFcmToken(fcmToken: String) {
        CTFcmMessageHandler().onNewToken(context, fcmToken)
        cleverTapAPI.pushFcmRegistrationId(fcmToken, true)
    }

    override suspend fun onUserLogin(id: String, values: Map<String, Any>) {
        cleverTapAPI.onUserLogin(values)
    }

    override suspend fun postEvent(eventName: String) {
        cleverTapAPI.pushEvent(eventName)
    }

    override suspend fun postEvent(eventName: String, value: String) {
        cleverTapAPI.pushEvent(eventName, mapOf("key" to value))
    }

    override suspend fun postEvent(eventName: String, values: Map<String, Any>) {
        cleverTapAPI.pushEvent(eventName, values)
    }

    override suspend fun sendPurchaseEvent(value: Float) {
        // No Impl for clevertap
    }

    override suspend fun setAttributionData(attributionData: Map<String, String>) {
        cleverTapAPI.pushProfile(attributionData)
    }

    override suspend fun setUserLogout() {
        // No Impl for clevertap
    }

    override suspend fun setUserProperties(properties: List<Pair<String, String>>) {
        val map = mutableMapOf<String, String>()
        properties.forEach {
            map[it.first] = it.second
        }
        cleverTapAPI.pushProfile(map.toMap())
    }
}
package com.jar.app.core_analytics

import android.os.Bundle
import android.os.Parcelable
import com.google.firebase.analytics.FirebaseAnalytics
import com.jar.internal.library.jarcoreanalytics.api.IAppAnalyticsService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsService @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : IAppAnalyticsService {

    override fun init() {

    }

    override suspend fun onUpdateFcmToken(fcmToken: String) {
        // No impl for this function
    }

    override suspend fun onUserLogin(id: String, values: Map<String, Any>) {
        firebaseAnalytics.setUserId(id)
        // No impl for this function
    }

    override suspend fun postEvent(eventName: String) {
        if (eventName == EventKey.FirstInvestment)
            firebaseAnalytics.logEvent(eventName, null)
    }

    override suspend fun postEvent(eventName: String, value: String) {
        val params = Bundle()
        params.putValue("value", value)
        firebaseAnalytics.logEvent(eventName, params)
    }

    override suspend fun postEvent(eventName: String, values: Map<String, Any>) {
        val params = Bundle()
        values.forEach { (key, value) ->
            params.putValue(key, value)
        }
        firebaseAnalytics.logEvent(eventName, params)
    }

    override suspend fun sendPurchaseEvent(value: Float) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE, Bundle().apply {
            putValue(FirebaseAnalytics.Param.CURRENCY, "INR")
            putValue(FirebaseAnalytics.Param.VALUE, value)
        })
    }

    override suspend fun setAttributionData(attributionData: Map<String, String>) {
        // No impl for this function
    }

    override suspend fun setUserLogout() {
        // No impl for this function
    }

    override suspend fun setUserProperties(properties: List<Pair<String, String>>) {
        properties.forEach { (key, value) ->
            if (key == "id") {
                firebaseAnalytics.setUserId(value)
            }
            firebaseAnalytics.setUserProperty(key, value)
        }
    }
}

private fun Bundle.putValue(key: String, value: Any) {
    when (value) {
        is Int -> this.putInt(key, value)
        is Boolean -> this.putBoolean(key, value)
        is Double -> this.putDouble(key, value)
        is Float -> this.putFloat(key, value)
        is Parcelable -> this.putParcelable(key, value)
        else -> this.putString(key, value.toString())
    }
}
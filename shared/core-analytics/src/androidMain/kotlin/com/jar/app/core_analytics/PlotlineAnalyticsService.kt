package com.jar.app.core_analytics

import android.content.Context
import com.clevertap.android.sdk.CleverTapAPI
import com.jar.internal.library.jarcoreanalytics.api.IAppAnalyticsService
import com.userexperior.UserExperior
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import so.plotline.insights.Plotline
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlotlineAnalyticsService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cleverTapAPI: CleverTapAPI
) : IAppAnalyticsService {

    override fun init() {
        //We are initializing Plotline in JarApp.kt -> Need user ID to initialize everytime
    }

    override suspend fun onUpdateFcmToken(fcmToken: String) {

    }

    override suspend fun onUserLogin(id: String, values: Map<String, Any>) {
        //When user will be logged out Plotline will not be initialized, Hence initializing here again
        //when user logins since the initialization in JarApp.kt has a check for userId
        initPlotLine(id)
        Plotline.identify(JSONObject(values))
    }

    override suspend fun postEvent(eventName: String) {
        Plotline.track(eventName)
    }

    override suspend fun postEvent(eventName: String, value: String) {
        Plotline.track(eventName, JSONObject().apply {
            put("action", value)
        })
    }

    override suspend fun postEvent(eventName: String, values: Map<String, Any>) {
        Plotline.track(eventName, JSONObject(values))
    }

    override suspend fun sendPurchaseEvent(value: Float) {

    }

    override suspend fun setAttributionData(attributionData: Map<String, String>) {

    }

    override suspend fun setUserLogout() {

    }

    override suspend fun setUserProperties(properties: List<Pair<String, String>>) {
        val propertiesJson = JSONObject()
        properties.forEach {
            propertiesJson.put(it.first, it.second)
        }
        Plotline.identify(propertiesJson)
    }

    private fun initPlotLine(id: String) {
        Plotline.init(context, CoreAnalyticsBuildKonfig.PLOT_LINE_KEY, id)
        Plotline.setPlotlineEventsListener { eventName, eventProperties ->
            try {
                //We are logging the callback events from Plotline in this listeners
                //(such as popup, widget, tooltip shown and clicked,etc.) in
                //Clever Tap and User Experior for better tracking
                cleverTapAPI.pushEvent(eventName, eventProperties.toMap())
                UserExperior.logEvent(eventName, eventProperties)
            } catch (_: Exception) { }
        }
    }

    private fun JSONObject.toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        val keys = this.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = this.get(key)
            if (value is JSONObject) {
                map[key] = value.toMap()
            } else {
                map[key] = value
            }
        }
        return map
    }

}
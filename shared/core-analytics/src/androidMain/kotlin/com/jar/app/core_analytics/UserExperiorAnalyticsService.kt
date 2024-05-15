package com.jar.app.core_analytics

import android.content.Context
import com.jar.internal.library.jarcoreanalytics.api.IAppAnalyticsService
import com.userexperior.UserExperior
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserExperiorAnalyticsService @Inject constructor(
    @ApplicationContext private val context: Context
) : IAppAnalyticsService {

    override fun init() {
        UserExperior.startRecording(context, CoreAnalyticsBuildKonfig.USER_EXPERIOR_PROJECT_KEY)
    }

    override suspend fun onUpdateFcmToken(fcmToken: String) {
    }

    override suspend fun onUserLogin(id: String, values: Map<String, Any>) {
        UserExperior.setUserIdentifier(id)
    }

    override suspend fun postEvent(eventName: String) {
        UserExperior.logEvent(eventName);
    }

    override suspend fun postEvent(eventName: String, value: String) {
        UserExperior.logEvent(eventName, hashMapOf<String, Any>("key" to value))
    }

    override suspend fun postEvent(eventName: String, values: Map<String, Any>) {
        UserExperior.logEvent(eventName, HashMap(values))
    }

    override suspend fun sendPurchaseEvent(value: Float) {
        // No Impl for user experior
    }

    override suspend fun setAttributionData(attributionData: Map<String, String>) {
        UserExperior.setUserProperties(HashMap(attributionData))
    }

    override suspend fun setUserLogout() {
        // No Impl for user experior
    }

    override suspend fun setUserProperties(properties: List<Pair<String, String>>) {
        val map = hashMapOf<String, String>()
        properties.forEach {
            map[it.first] = it.second
        }
        UserExperior.setUserProperties(HashMap(map))
    }
}
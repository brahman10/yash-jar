package com.jar.app.core_analytics.di

import android.content.Context
import com.appsflyer.AppsFlyerLib
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.pushnotification.PushConstants
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.jar.app.core_analytics.BuildConfig
import com.jar.app.core_analytics.CoreAnalyticsBuildKonfig
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AnalyticsModule {

    companion object {

        private const val ANALYTICS_LOG_TAG = "#ANALYTICS_LOG_TAG#"

        @Provides
        @Singleton
        internal fun provideClevertap(@ApplicationContext context: Context): CleverTapAPI {
            CleverTapAPI.changeXiaomiCredentials(
                CoreAnalyticsBuildKonfig.XIAOMI_APP_ID,
                CoreAnalyticsBuildKonfig.XIAOMI_APP_KEY
            )
            CleverTapAPI.enableXiaomiPushOn(PushConstants.XIAOMI_MIUI_DEVICES)
            return CleverTapAPI.getDefaultInstance(context.applicationContext)!!
        }

        @Provides
        @Singleton
        internal fun provideAppsFlyerLib(@ApplicationContext context: Context): AppsFlyerLib {
            return AppsFlyerLib.getInstance()
        }

        @Provides
        @Singleton
        internal fun provideFirebaseAnalytics(): FirebaseAnalytics {
            return Firebase.analytics
        }

        @Provides
        @Singleton
        internal fun provideAnalyticsApi(): AnalyticsApi {
            return AnalyticsApi.getInstance(
                logTag = ANALYTICS_LOG_TAG,
                enableEventLoggingInLogcat = BuildConfig.DEBUG
            )
        }
    }
}
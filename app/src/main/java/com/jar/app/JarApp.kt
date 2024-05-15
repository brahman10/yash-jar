package com.jar.app

import android.app.Application
import android.content.ComponentName
import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.ConfigurationCompat
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Configuration
import com.appsflyer.AppsFlyerLib
import com.clevertap.android.pushtemplates.PushTemplateNotificationHandler
import com.clevertap.android.sdk.ActivityLifecycleCallback
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.interfaces.NotificationHandler
import com.facebook.FacebookSdk
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.jar.app.base.AppLifecycleListener
import com.jar.app.base.BuildConfig
import com.jar.app.base.util.toMap
import com.jar.app.core_base.domain.model.User
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.core_analytics.CoreAnalyticsBuildKonfig
import com.jar.app.core_analytics.EventKey.SESSION_URL
import com.jar.app.core_analytics.EventKey.USER_EXPERIOR_SESSION_STARTED
import com.jar.app.core_base.util.RemoteConfigDefaultsHelper
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.event.RemoteConfigRefreshEvent
import com.jar.app.feature.home.domain.usecase.FetchActiveAnalyticsListUseCase
import com.jar.app.feature.home.domain.usecase.UpdateFcmTokenUseCase
import com.jar.app.feature_sms_sync.impl.receiver.NewSmsReceiver
import com.jar.app.receiver.BootReceiver
import com.jar.app.util.TimberExceptionFirebaseLog
import com.jar.app.worker.ConfigInitializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.userexperior.UserExperior
import com.userexperior.interfaces.recording.UserExperiorListener
import dagger.Lazy
import dagger.hilt.android.HiltAndroidApp
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import so.plotline.insights.Plotline
import timber.log.Timber
import javax.inject.Inject
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi as InternalAnalyticsApi

@HiltAndroidApp
internal class JarApp : Application(), Configuration.Provider {

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var updateFcmTokenUseCase: UpdateFcmTokenUseCase

    @Inject
    lateinit var appsFlyerLib: AppsFlyerLib

    @Inject
    lateinit var remoteConfigDefaultsHelper: RemoteConfigDefaultsHelper

    @Inject
    lateinit var internalAnalyticsApi: Lazy<InternalAnalyticsApi>

    @Inject
    lateinit var analyticsInitializer: Lazy<AnalyticsInitializer>

    @Inject
    lateinit var configInitializer: Lazy<ConfigInitializer>

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var fetchActiveAnalyticsListUseCase: FetchActiveAnalyticsListUseCase

    @Inject
    lateinit var cleverTapAPI: CleverTapAPI

    @Inject
    lateinit var appLifecycleListener: Lazy<AppLifecycleListener>

    @Inject
    lateinit var json: Json

    @Inject
    lateinit var serializer: Serializer

    lateinit var appScope: CoroutineScope

    var appStartTime: Long = 0L
    var hasFiredStartTimeEvent = false

    var isAuthenticationRequestDone = false

    //To skip missed you screen when language is changed on onboarding screen
    var skipMissedYouScreen = false

    override fun onCreate() {
        ActivityLifecycleCallback.register(this)
        super.onCreate()
        init()
        appStartTime = System.currentTimeMillis()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        val builder = Configuration.Builder().setWorkerFactory(workerFactory)
        if (BuildConfig.DEBUG) builder.setMinimumLoggingLevel(Log.DEBUG)
        return builder.build()
    }

    private fun init() {
        initDarkTheme()
        initLanguagePreferences()
        initAppScope()
        initLogger()
        initFbSdk()
        initAppsFlyer() //Need to init AppsFlyer before Analytics SDK
        initPlotline()
        cleverTapAPI.suspendInAppNotifications()
        CleverTapAPI.setNotificationHandler(PushTemplateNotificationHandler() as NotificationHandler)
        registerApplicationToPlotline()
        initAnalyticsSdk()
        initConfigSdk()
        initFirebaseMessaging()
        initUserExperiorListener()
        registerEventBus()
        enableBootReceiver()
        enableSmsReceiver()
        startMonitoringAppStateEvents()
        setupClevertapLogging()
    }

    private fun initUserExperiorListener() {
        UserExperior.setUserExperiorListener {
            val sessionUrl = UserExperior.getSessionUrl("CleverTap")
            sessionUrl?.let {
                cleverTapAPI.pushEvent(
                    USER_EXPERIOR_SESSION_STARTED, mapOf<String, String>(
                        SESSION_URL to it
                    )
                )
            }
        }
    }
    private fun registerApplicationToPlotline() {
        /**
         * Plotine SDK needs to be registered before HomeActivity is loaded !!
         * Added this here since we are initializing Plotline based on Active Analytics API
         * which may give response after HomeActivity is loaded (which results in tracking errors in this case)
         * */
        Plotline.registerApplication(this)
    }

    private fun initAppsFlyer() {
        appsFlyerLib.start(applicationContext)
        appsFlyerLib.init(
            CoreAnalyticsBuildKonfig.APPSFLYER_DEV_KEY,
            null,
            applicationContext
        )
    }

    private fun initPlotline() {
        val userString = prefs.getUserStringSync()
        if (userString.isNullOrBlank().not()) {
            serializer.decodeFromString<User?>(userString!!)?.userId?.let {
                Plotline.init(this, CoreAnalyticsBuildKonfig.PLOT_LINE_KEY, it)
                Plotline.setPlotlineEventsListener { eventName, eventProperties ->
                    try {
                        cleverTapAPI.pushEvent(eventName, eventProperties.toMap())
                    } catch (_: Exception) { }
                }
            }
        }
    }

    private fun setupClevertapLogging() {
        if (com.jar.app.BuildConfig.DEBUG) {
            CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.DEBUG)
        } else {
            CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.OFF)
        }
    }

    private fun initDarkTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    private fun initAppScope() {
        appScope = GlobalScope
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Timber.plant(TimberExceptionFirebaseLog())
    }

    private fun initFirebaseMessaging() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.e(task.exception)
                return@addOnCompleteListener
            }
            Firebase.analytics.appInstanceId.addOnCompleteListener { appInstanceIdTask ->
                val appInstanceId =
                    if (appInstanceIdTask.isSuccessful) appInstanceIdTask.result else null
                val token = task.result
                if (!token.isNullOrBlank()) {
                    Timber.d("new fcm token : $token")
                    appScope.launch {
                        if (prefs.isLoggedIn()) {
                            updateFcmTokenUseCase.updateFcmToken(token, appInstanceId)
                                .collect(onSuccess = {
                                    Timber.d("Token Updated")
                                    prefs.setFCMToken(token)
                                })
                        }
                    }
                    internalAnalyticsApi.get().updateFcmToken(token)
                }
            }
        }
    }

    private fun registerEventBus() {
        EventBus.getDefault().register(this)
    }

    private fun initLanguagePreferences() {
        val selectedLanguage = prefs.getCurrentLanguageCode()
        val deviceLanguageCode =
            ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]?.language ?: "en"
        if (selectedLanguage.isBlank()) {
            prefs.setSelectedLanguageCode(deviceLanguageCode)
            //To set the language code in moko resource
            StringDesc.localeType = StringDesc.LocaleType.Custom(deviceLanguageCode)
        }
    }

    private fun initAnalyticsSdk() {
        analyticsInitializer.get().initializeAnalyticsSdk()
    }

    private fun initConfigSdk() {
        configInitializer.get().initialize(
            defaults = getDefaultConfigList()
        )
        configInitializer.get().fetchConfig(
            onSuccess = {
                Timber.d("FETCH CONFIG FROM SERVER SUCCESS")
            },
            onError = {
                Timber.e(it)
            }
        )
    }

    private fun getDefaultConfigList(): List<Pair<String, String>> {
        val jsonString = remoteConfigDefaultsHelper.getRemoteConfigDefaultsFile().readText(context = applicationContext)
        return remoteConfigDefaultsHelper.convertToList(jsonString)
    }

    private fun enableBootReceiver() {
        val receiver = ComponentName(applicationContext, BootReceiver::class.java)
        applicationContext.packageManager.setComponentEnabledSetting(
            receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
        )
    }

    private fun enableSmsReceiver() {
        val receiver2 = ComponentName(applicationContext, NewSmsReceiver::class.java)
        applicationContext.packageManager.setComponentEnabledSetting(
            receiver2, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
        )
    }

    private fun initFbSdk() {
        FacebookSdk.setAutoInitEnabled(true)
        FacebookSdk.fullyInitialize()
    }

    private fun startMonitoringAppStateEvents() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleListener.get())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRemoteConfigRefreshEvent(remoteConfigRefreshEvent: RemoteConfigRefreshEvent) {
        configInitializer.get().fetchConfig(
            onSuccess = {
                Timber.d("Remote config refreshed")
            },
            onError = {
                Timber.e(it)
            }
        )
    }
}
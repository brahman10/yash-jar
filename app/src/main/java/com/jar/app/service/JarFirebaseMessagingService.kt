package com.jar.app.service

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.pushnotification.fcm.CTFcmMessageHandler
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jar.app.R
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.event.RemoteConfigRefreshEvent
import com.jar.app.feature.home.domain.usecase.UpdateFcmTokenUseCase
import com.jar.app.util.NotificationUtil
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
internal class JarFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var appScope: CoroutineScope

    @Inject
    lateinit var updateFcmTokenUseCase: UpdateFcmTokenUseCase

    @Inject
    lateinit var notificationUtil: NotificationUtil

    @Inject
    lateinit var analyticsHandler: Lazy<AnalyticsApi>

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        appScope.launch {
            val appInstanceId = Firebase.analytics.appInstanceId.await()
            if (prefs.isLoggedIn()) {
                updateFcmTokenUseCase.updateFcmToken(token, appInstanceId).collect(
                    onSuccess = {
                        Timber.d("Token Updated Successfully")
                    }
                )
            }
            analyticsHandler.get().updateFcmToken(token)
        }

        FirebaseMessaging.getInstance()
            .subscribeToTopic(BaseConstants.REMOTE_CONFIG_FORCE_REFRESH_TOPIC)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.data[BaseConstants.PUSH_NOTIFICATION_CONTEXT]?.let {
            prefs.setPushNotificationContext(it)
        }
        val pnCustomSound = remoteMessage.data[BaseConstants.PUSH_NOTIFICATION_SOUND]

        if (remoteMessage.data.containsKey(BaseConstants.REMOTE_CONFIG_FORCE_REFRESH_KEY)) {
            prefs.setRemoteConfigStaleState(true)
            EventBus.getDefault().post(RemoteConfigRefreshEvent())
            return
        }
        val channelName = getString(R.string.clevertap_notification_channel_name)
        val isOreoOrHigher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        val shouldUseCustomSound = pnCustomSound.isNullOrBlank().not()

        if (isOreoOrHigher) {
            if (shouldUseCustomSound) {
                createNotificationChannelWithSound(channelName, pnCustomSound!!)
            } else {
                createNotificationChannel(channelName)
            }
        } else {
            createNotificationChannel(channelName)
        }

        if (handleCleverTap(remoteMessage)) return

        val remoteTitle = remoteMessage.notification?.title
        if (remoteTitle.isNullOrBlank()) return

        notificationUtil.sendNotification(
            remoteTitle,
            remoteMessage.notification?.body,
            remoteMessage.data
        )
    }

    private fun createNotificationChannel(channelName: String) {
        CleverTapAPI.createNotificationChannel(
            applicationContext,
            channelName,
            channelName,
            channelName,
            NotificationManagerCompat.IMPORTANCE_MAX,
            true
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannelWithSound(channelName: String, customSound: String) {
        CleverTapAPI.createNotificationChannel(
            applicationContext,
            channelName,
            channelName,
            channelName,
            NotificationManagerCompat.IMPORTANCE_MAX,
            true,
            customSound
        )
    }

    private fun handleCleverTap(message: RemoteMessage): Boolean {
        message.data.apply {
            return try {
                if (size > 0) {
                    val extras = Bundle()
                    for ((key, value) in this) {
                        extras.putString(key, value)
                    }
                    val info = CleverTapAPI.getNotificationInfo(extras)
                    if (info.fromCleverTap) {
                        CTFcmMessageHandler().createNotification(applicationContext, message)
                    } else {
                        false
                    }
                } else false
            } catch (t: Throwable) {
                FirebaseCrashlytics.getInstance().recordException(t)
                false
            }
        }
    }
}
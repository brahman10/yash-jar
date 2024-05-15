package com.jar.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.jar.app.R
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature.home.ui.activity.HomeActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NotificationUtil @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val DEFAULT_NOTIFICATION_CHANNEL_NAME = "Change Jar"
        private const val DEFAULT_NOTIFICATION_ID = 1337
        const val ONBOARDING_REMINDER_NOTIFICATION_ID = 1338
    }

    private var channel: NotificationChannel? = null

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String): NotificationChannel? {
        if (channel == null) {
            channel = NotificationChannel(
                channelId,
                DEFAULT_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
        }
        return channel
    }

    fun sendNotification(
        messageTitle: String,
        messageDescription: String? = null,
        data: Map<String, String>? = null,
        notificationId:Int= DEFAULT_NOTIFICATION_ID
    ) {
        val intent = HomeActivity.newIntent(context)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        intent.putExtra(BaseConstants.DEEPLINK_EXTRACTION_KEY, data?.get(BaseConstants.DEEPLINK_EXTRACTION_KEY))
        intent.putExtra(BaseConstants.PUSH_NOTIFICATION_CONTEXT, data?.get(BaseConstants.PUSH_NOTIFICATION_CONTEXT))

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            } else PendingIntent.FLAG_ONE_SHOT
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        var contentTitle: String = messageTitle

        if (contentTitle.isEmpty()) {
            contentTitle = context.getString(R.string.app_name)
        }

        val channelId = context.getString(R.string.default_notification_channel_id)

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification_colored)
            .setContentTitle(contentTitle)
            .setContentText(messageDescription)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(channelId)?.let {
                notificationManager.createNotificationChannel(it)
            }
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    fun cancelNotification(notificationId:Int){
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }
}
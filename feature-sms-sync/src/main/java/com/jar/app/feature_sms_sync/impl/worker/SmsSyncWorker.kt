package com.jar.app.feature_sms_sync.impl.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.jar.app.feature_sms_sync.R
import com.jar.app.feature_sms_sync.impl.utils.SmsSyncUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
internal class SmsSyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val smsSyncUtils: SmsSyncUtils
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            smsSyncUtils.readRecentMessages()
            Result.success()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SMS_NOTIFICATION_CHANNEL_ID,
                SMS_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, SMS_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.feature_sms_sync_ic_notification)
            .setOngoing(true)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setContentTitle(context.getString(com.jar.app.base.R.string.app_name))
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .setContentText("Syncing data...")
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(
                NOTIFICATION_ID,
                notification
            )
        }
    }

    companion object {
        const val SMS_SYNC_WORKER_NAME = "SmsSyncWorker"
        const val SMS_SYNC_WORKER_TAG = "sms_sync_worker"

        private const val SMS_NOTIFICATION_CHANNEL_ID = "DATA_SYNC_CHANNEL"
        private const val SMS_NOTIFICATION_CHANNEL_NAME = "Data Sync Channel"

        private const val NOTIFICATION_ID = 1962
    }
}
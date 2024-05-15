package com.jar.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.jar.app.R
import com.jar.app.util.NotificationUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.*
import java.util.concurrent.TimeUnit

@HiltWorker
internal class ScheduleNotificationWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParameters: WorkerParameters,
    private val notificationUtil: NotificationUtil,
    private val workManager: WorkManager,
) : Worker(appContext, workerParameters) {

    override fun doWork(): Result {
        notificationUtil.sendNotification(
            messageTitle = inputData.getString(PARAM_TITLE) ?: appContext.getString(R.string.app_name),
            messageDescription = inputData.getString(PARAM_DESCRIPTION),
            notificationId = NotificationUtil.ONBOARDING_REMINDER_NOTIFICATION_ID
        )
        // Set Execution around 08:15:00 PM for 3 days
        if (inputData.getInt(PARAM_ATTEMPT_COUNT, 0) < 3) {
            val currentDate = Calendar.getInstance()
            val dueDate = Calendar.getInstance()
            dueDate.set(Calendar.HOUR_OF_DAY, 20)
            dueDate.set(Calendar.MINUTE, 15)
            dueDate.set(Calendar.SECOND, 0)
            if (dueDate.before(currentDate)) {
                dueDate.add(Calendar.HOUR_OF_DAY, 24)
            }
            val initialDelay = dueDate.timeInMillis - currentDate.timeInMillis
            val workRequest = OneTimeWorkRequestBuilder<ScheduleNotificationWorker>()
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .addTag(inputData.getString(PARAM_WORK_NAME) ?: WORKER_TAG)
                .setInputData(
                    Data.Builder()
                        .putString(PARAM_DESCRIPTION, inputData.getString(PARAM_DESCRIPTION))
                        .putInt(PARAM_ATTEMPT_COUNT, (inputData.getInt(PARAM_ATTEMPT_COUNT, 0) + 1))
                        .putString(PARAM_WORK_NAME, inputData.getString(PARAM_WORK_NAME) ?: WORKER_TAG)
                        .build()
                )
                .build()
            workManager.enqueueUniqueWork(
                inputData.getString(PARAM_WORK_NAME) ?: WORKER_TAG,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        } else
            workManager.cancelAllWorkByTag(WORK_NAME_ONBOARDING_REMINDER)
        return Result.success()
    }

    companion object {
        private const val WORKER_TAG = "notification_scheduler"

        //Add unique names for different requests, which will help in cancellation by name or tag
        const val WORK_NAME_ONBOARDING_REMINDER = "onboardingReminder"

        const val PARAM_TITLE = "title"
        const val PARAM_DESCRIPTION = "description"
        const val PARAM_ATTEMPT_COUNT = "attempt_count"
        const val PARAM_WORK_NAME = "work_name"
    }
}
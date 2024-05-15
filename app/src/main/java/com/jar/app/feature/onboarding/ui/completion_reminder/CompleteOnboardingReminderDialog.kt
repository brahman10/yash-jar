package com.jar.app.feature.onboarding.ui.completion_reminder

import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.jar.app.R
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.databinding.DialogCompleteOnboardingReminderBinding
import com.jar.app.worker.ScheduleNotificationWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class CompleteOnboardingReminderDialog :
    BaseBottomSheetDialogFragment<DialogCompleteOnboardingReminderBinding>() {

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> DialogCompleteOnboardingReminderBinding
        get() = DialogCompleteOnboardingReminderBinding::inflate

    private val timeInit = System.currentTimeMillis()

    override val bottomSheetConfig = DEFAULT_CONFIG

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var workManager: WorkManager

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    override fun onDismiss(dialog: DialogInterface) {
        analyticsHandler.postEvent(
            EventKey.Exit_BottomScreen_Onboarding,
            mapOf(EventKey.TIME_SPENT to System.currentTimeMillis() - timeInit)
        )
        super.onDismiss(dialog)
    }

    override fun setup() {
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnClose.setDebounceClickListener {
            dismissAllowingStateLoss()
            analyticsHandler.postEvent(EventKey.ClickedCrossButton_BottomScreen_Exit_Onboarding)
        }
        binding.btnSignUpNow.setDebounceClickListener {
            dismissAllowingStateLoss()
            analyticsHandler.postEvent(EventKey.CONTINUE_CLICKED)
        }
        binding.btnMaybeLater.setDebounceClickListener {
            analyticsHandler.postEvent(EventKey.EXIT_CLICKED)
            if (remoteConfigApi.isOnboardingReminderNotifiEnabled())
                scheduleNotification()
            dismissAllowingStateLoss()
            requireActivity().finishAffinity()
        }
    }

    private fun scheduleNotification() {
        val workRequest = OneTimeWorkRequestBuilder<ScheduleNotificationWorker>()
            .addTag(ScheduleNotificationWorker.WORK_NAME_ONBOARDING_REMINDER)
            .setInitialDelay(20L, TimeUnit.MINUTES)
            .setInputData(
                Data.Builder()
                    .putString(
                        ScheduleNotificationWorker.PARAM_DESCRIPTION,
                        getString(R.string.come_back_start_journey)
                    )
                    .putInt(ScheduleNotificationWorker.PARAM_ATTEMPT_COUNT, 0)
                    .putString(
                        ScheduleNotificationWorker.PARAM_WORK_NAME,
                        ScheduleNotificationWorker.WORK_NAME_ONBOARDING_REMINDER
                    )
                    .build()
            )
            .build()
        workManager.enqueueUniqueWork(
            ScheduleNotificationWorker.WORK_NAME_ONBOARDING_REMINDER,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}
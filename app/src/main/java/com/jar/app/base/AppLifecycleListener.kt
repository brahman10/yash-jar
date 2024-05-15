package com.jar.app.base

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.jar.app.base.util.countDownTimer
import com.jar.app.base.util.doRepeatingTask
import com.jar.app.base.util.milliSecondsToCountDown
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.threeten.bp.Duration
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLifecycleListener @Inject constructor(
    private val analyticsHandler: AnalyticsApi,
    private val appScope: CoroutineScope
) : DefaultLifecycleObserver {

    companion object {
        private const val SESSION_LENGTH = "SESSION_LENGTH"
        private const val CUSTOM_APP_OPEN_EVENT = "CUSTOM_APP_OPEN_EVENT"
        private const val CUSTOM_APP_CLOSED_EVENT = "CUSTOM_APP_CLOSED_EVENT"
    }

    private var appUsageTimerJob: Job? = null

    private var backgroundDeltaJob: Job? = null

    private var appUsageTime: Int = 0

    private var bgTime: Int = 0

    override fun onStart(owner: LifecycleOwner) {
        analyticsHandler.postEvent(CUSTOM_APP_OPEN_EVENT)
        startMeasuringAppUsageTime()
        backgroundDeltaJob?.cancel()
    }

    override fun onStop(owner: LifecycleOwner) {
        startBackgroundDelta()
    }

    private fun startMeasuringAppUsageTime() {
        appUsageTimerJob?.cancel()
        appUsageTimerJob = appScope.doRepeatingTask(repeatInterval = 1000) {
            appUsageTime++
        }
    }

    private fun startBackgroundDelta() {
        backgroundDeltaJob?.cancel()
        backgroundDeltaJob = appScope.countDownTimer(totalMillis = Duration.ofMinutes(5).toMillis(),
            onInterval = {
                bgTime++
                Timber.d("BG DELTA : ${it.milliSecondsToCountDown()}")
            },
            onFinished = {
                appUsageTime -= bgTime
                bgTime = 0
                fireSessionTimeEvent()
                stopAllTimerJobs()
            }
        )
    }

    private fun stopAllTimerJobs() {
        appUsageTimerJob?.cancel()
        backgroundDeltaJob?.cancel()
    }

    private fun fireSessionTimeEvent() {
        analyticsHandler.postEvent(
            event = CUSTOM_APP_CLOSED_EVENT,
            values = mapOf(
                Pair(SESSION_LENGTH, appUsageTime)
            ),
            shouldPushOncePerSession = true
        )
        appUsageTime = 0
    }

}
package com.jar.app.feature.home.domain.model

import kotlinx.serialization.SerialName
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset

@kotlinx.serialization.Serializable
data class DowntimeResponse(
    @SerialName("startTime")
    val startTime: Long,
    @SerialName("endTime")
    val endTime: Long
) {

    fun isAppUnderDowntimeMaintenance(): Boolean {
        val startInstant = Instant.ofEpochSecond(startTime).atOffset(ZoneOffset.UTC)
        val endInstant = Instant.ofEpochSecond(endTime).atOffset(ZoneOffset.UTC)
        val currentInstant = Instant.now().atOffset(ZoneOffset.UTC)
        return currentInstant.isAfter(startInstant) && currentInstant.isBefore(endInstant)
    }

    fun isDowntimeScheduledInFuture(): Boolean {
        val startInstant = Instant.ofEpochSecond(startTime).atOffset(ZoneOffset.UTC)
        val currentInstant = Instant.now().atOffset(ZoneOffset.UTC)
        return startInstant.isAfter(currentInstant)
    }

    fun getTimeLeftTillDowntimeStartsInMillis(): Long {
        val startInstant = Instant.ofEpochSecond(startTime).atOffset(ZoneOffset.UTC)
        val currentInstant = Instant.now().atOffset(ZoneOffset.UTC)
        return Duration.between(currentInstant, startInstant).toMillis()
    }

    fun getTimeLeftTillDowntimeEndsInMillis(): Long {
        val endInstant = Instant.ofEpochSecond(endTime).atOffset(ZoneOffset.UTC)
        val currentInstant = Instant.now().atOffset(ZoneOffset.UTC)
        return Duration.between(currentInstant, endInstant).toMillis()
    }
}
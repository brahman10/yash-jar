package com.jar.app.base.util

import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import java.util.Calendar

fun checkIfEpochTimeIsYesterday(epochMillis: Long): Boolean {
    val epochDate = Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate()
    val yesterday = LocalDate.now(ZoneId.systemDefault()).minusDays(1)
    return epochDate.isEqual(yesterday)
}

fun isInLastHour(epochMillis: Long): Boolean {
    val epochCalendar = Calendar.getInstance().apply { timeInMillis = epochMillis }
    val oneHourAgo = Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, -1) }
    return !epochCalendar.before(oneHourAgo)
}
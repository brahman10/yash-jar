package com.jar.app.feature_goal_based_saving.impl.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun convertEpochToCustomFormat(epochTime: Long): String {
    val dateFormat = SimpleDateFormat("d MMM''yy", Locale.getDefault())
    return dateFormat.format(Date(epochTime * 1000))
}
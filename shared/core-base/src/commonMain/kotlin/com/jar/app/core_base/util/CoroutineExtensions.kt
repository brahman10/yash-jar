package com.jar.app.core_base.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun CoroutineScope.countDownTimer(
    totalMillis: Long,
    intervalInMillis: Long = 1000,
    onInterval: suspend (millisLeft: Long) -> Unit = {},
    onFinished: suspend () -> Unit = {},
    onPaused: suspend () -> Boolean = { false },
) = this.launch(Dispatchers.Default) {
    var total = totalMillis
    while (isActive) {
        if (!onPaused()) {
            if (total > 0) {
                withContext(Dispatchers.Main) {
                    onInterval(total)
                }
                delay(intervalInMillis)
                total -= intervalInMillis
            } else {
                withContext(Dispatchers.Main) {
                    onFinished()
                    cancel("Task Completed")
                }
            }
        }
    }
}
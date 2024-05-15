package com.jar.app.core_base.util

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import kotlin.math.abs
import kotlin.math.log10

fun Int?.orZero() = this ?: 0

fun Long?.orZero() = this ?: 0L

fun Float?.orZero() = this ?: 0f

fun Double?.orZero() = this ?: 0.0

fun Float.roundUp(n: Long): Float {
    val decimalMode = DecimalMode(
        decimalPrecision = this.toInt().length().toLong(),
        roundingMode = RoundingMode.CEILING,
        scale = n
    )
    return BigDecimal.fromFloat(this, decimalMode).floatValue(exactRequired = false)
}
fun Float.roundUpToPlainString(n: Long): String {
    val decimalMode = DecimalMode(
        decimalPrecision = this.toInt().length().toLong(),
        roundingMode = RoundingMode.CEILING,
        scale = n
    )
    return BigDecimal.fromFloat(this, decimalMode).toPlainString()
}

fun Float.roundDown(n: Long): Float {
    val decimalMode = DecimalMode(
        decimalPrecision = this.toInt().length().toLong(),
        roundingMode = RoundingMode.FLOOR,
        scale = n
    )
    return BigDecimal.fromFloat(this, decimalMode).floatValue(exactRequired = false)
}

fun Int.length() = when (this) {
    0 -> 1
    else -> log10(abs(toDouble())).toInt() + 1
}

fun Int.milliSecondsToCountDown(showZero: Boolean = false): String {
    val seconds = this / 1000
    val hour = seconds / 3600
    val min = (seconds / 60) % 60
    val sec = seconds % 60
    val min0 = if (min < 10) "0" else ""
    val sec0 = if (sec < 10) "0" else ""
    val hourStr = when (hour) {
        0 -> {
            if (showZero) "00:" else ""
        }

        in 1..9 -> "0$hour:"
        else -> "$hour:"
    }
    return "$hourStr$min0$min:$sec0$sec"
}

fun Float.addPercentage(percentage: Float): Float {
    return (this * (1 + (percentage / 100)))
}

fun Int.toBoolean(): Boolean {
    return this != 0
}

fun Long.toBoolean(): Boolean {
    return this != 0L
}
package com.jar.app.feature_spin.impl.custom.util

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import kotlin.math.roundToInt

fun Float.dpToPx(context: Context?): Int {
    val r: Resources? = context?.resources
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this, r?.displayMetrics
    ).roundToInt()
}

fun Context.getDimension(value: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value, this.resources.displayMetrics).toInt()
}

fun Int.isJackpot(): Boolean {
    return this == JACKPOT
}

fun Int.isOhNo(): Boolean {
    return this == OH_NO
}

fun Int?.isFlat(): Boolean {
    return this == OH_NO || this != JACKPOT
}

fun Int.isJackpotOrOhNo(): Boolean {
    return this == JACKPOT || this == 0
}

fun Int?.isNotJackpotAndIsNull(): Boolean {
    return this != JACKPOT
}

fun Int?.isNotOhNoNotJackpotAndIsNotNull(): Boolean{
    return this != JACKPOT && this != OH_NO && this != null
}
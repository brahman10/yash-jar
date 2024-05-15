package com.jar.app.core_ui.util

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan

fun createColoredSpannableString(text: String, color: Int): SpannableString {
    val spannableString = SpannableString(text)
    val foregroundColorSpan = ForegroundColorSpan(color)
    spannableString.setSpan(
        foregroundColorSpan,
        0,
        spannableString.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannableString
}

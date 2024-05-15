package com.jar.app.core_ui.text_styling

import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorInt

class RelativeColorSpan(@ColorInt private val color: Int, private val alpha: Int) :
    ForegroundColorSpan(color) {
    override fun updateDrawState(textPaint: TextPaint) {
        super.updateDrawState(textPaint)
        textPaint.color = color
        textPaint.alpha = alpha
    }
}
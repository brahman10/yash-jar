package com.jar.app.core_ui.text_styling

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorInt
import androidx.core.graphics.TypefaceCompat
import java.security.AccessController.getContext
import kotlin.coroutines.coroutineContext

class RelativeBoldSpan(@ColorInt private val color: Int,private val typeFace : Typeface? = null) :
    ForegroundColorSpan(color) {
    override fun updateDrawState(textPaint: TextPaint) {
        super.updateDrawState(textPaint)
        typeFace?.let {
            textPaint.setTypeface(it)
        }?: kotlin.run {
            textPaint.isFakeBoldText = true
        }
    }
}
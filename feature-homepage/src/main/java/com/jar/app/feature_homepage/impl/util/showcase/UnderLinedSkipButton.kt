package com.jar.app.feature_homepage.impl.util.showcase

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.underline

class UnderLinedSkipButton(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    private val underlinedText: CharSequence = buildSpannedString {
        underline {
            append(context.getString(com.jar.app.core_ui.R.string.core_ui_skip))
        }
    }

    private val textBounds: Rect = Rect()

    init {
        paint.textSize =
            resources.getDimensionPixelSize(com.jar.app.core_ui.R.dimen.dimen_16sp).toFloat()
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.color = ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_ACA1D3)
        paint.getTextBounds(underlinedText.toString(), 0, underlinedText.length, textBounds)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Calculate the x and y coordinates for drawing the text centered
        val x = (width - textBounds.width()) / 2f
        val y = (height + textBounds.height()) / 2f

        // Draw the underlined text
        canvas.drawText(underlinedText.toString(), x, y, paint)
        canvas.drawText("_".repeat(underlinedText.length), x + 2, y + 8, paint)
    }

}

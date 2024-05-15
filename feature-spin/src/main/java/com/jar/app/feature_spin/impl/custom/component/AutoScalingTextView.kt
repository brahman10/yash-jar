package com.jar.app.feature_spin.impl.custom.component

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView

internal class AutoScalingTextView2(context: Context, attrs: AttributeSet? = null) :
    AppCompatTextView(context, attrs) {

    private val paint = Paint()
    private val bounds = Rect()
    private val minTextSize = 50f // Set a minimum text size (in SP) to prevent the text from becoming too small

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

        adjustTextSize(availableWidth, availableHeight)
    }

    private fun adjustTextSize(availableWidth: Int, availableHeight: Int) {
        var newTextSize = textSize
        getTextBounds(newTextSize)

        while ((bounds.width() > availableWidth || bounds.height() > availableHeight) && newTextSize > minTextSize) {
            newTextSize -= 1f
            getTextBounds(newTextSize)
        }

        setTextSize(TypedValue.COMPLEX_UNIT_PX, newTextSize)
    }

    private fun getTextBounds(textSize: Float) {
        paint.textSize = textSize
        paint.getTextBounds(text.toString(), 0, text.length, bounds)
    }
}


package com.jar.app.core_ui.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet

class VerticalTextView(
    context: Context,
    attrs: AttributeSet
) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {

    private val textPaint = paint

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        super.onMeasure(
            heightMeasureSpec,
            widthMeasureSpec
        )
        setMeasuredDimension(
            measuredHeight,
            measuredWidth
        )
    }

    override fun onDraw(canvas: Canvas) {
        textPaint.color = currentTextColor
        textPaint.drawableState = drawableState
        canvas.save()
        canvas.translate(0f, height.toFloat())
        canvas.rotate(-90f)
        canvas.translate(
            compoundPaddingLeft.toFloat(),
            extendedPaddingTop.toFloat()
        )
        layout.draw(canvas)
        canvas.restore()
    }
}
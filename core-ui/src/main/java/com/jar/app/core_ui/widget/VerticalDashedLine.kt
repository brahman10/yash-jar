package com.jar.app.core_ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.jar.app.core_ui.R

class VerticalDashedLine @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {
    private val mPaint: Paint
    private var orientation = 0

    init {
        val dashGap: Int
        val dashLength: Int
        val dashThickness: Int
        val color: Int
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.VerticalDashedLine, 0, 0)
        try {
            dashGap = a.getDimensionPixelSize(R.styleable.VerticalDashedLine_dashGap, 5)
            dashLength = a.getDimensionPixelSize(R.styleable.VerticalDashedLine_dashLength, 5)
            dashThickness = a.getDimensionPixelSize(R.styleable.VerticalDashedLine_dashThickness, 3)
            color = a.getColor(R.styleable.VerticalDashedLine_color, -0x1000000)
            orientation =
                a.getInt(R.styleable.VerticalDashedLine_orientation, ORIENTATION_HORIZONTAL)
        } finally {
            a.recycle()
        }
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.color = color
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = dashThickness.toFloat()
        mPaint.pathEffect =
            DashPathEffect(floatArrayOf(dashLength.toFloat(), dashGap.toFloat()), 0f)
    }

    override fun onDraw(canvas: Canvas) {
        if (orientation == ORIENTATION_HORIZONTAL) {
            val center = height * .5f
            canvas.drawLine(0f, center, width.toFloat(), center, mPaint)
        } else {
            val center = width * .5f
            canvas.drawLine(center, 0f, center, height.toFloat(), mPaint)
        }
    }

    companion object {
        var ORIENTATION_HORIZONTAL = 0
        var ORIENTATION_VERTICAL = 1
    }
}
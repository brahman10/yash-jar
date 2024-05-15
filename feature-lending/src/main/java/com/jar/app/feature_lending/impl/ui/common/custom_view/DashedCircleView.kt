package com.jar.app.feature_lending.impl.ui.common.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.jar.app.base.util.dp
import com.jar.app.feature_lending.R


class DashedCircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var p: Paint = Paint()
    private var mDashPath: DashPathEffect? = null
    private val mRadius = 60.dp
    private val mSize = 120.dp
    private val dashPortion = 0.90f
    private val gapPortion = 0.10f
    private val mStrokeWidth = 25f

    fun setDashCount(dashCount: Int) {
        val circumference = 2 * Math.PI * (mRadius - 5.dp)
        val dashPlusGapSize = (circumference / dashCount).toFloat()
        mDashPath = DashPathEffect(
            floatArrayOf(
                dashPlusGapSize * dashPortion,
                dashPlusGapSize * gapPortion
            ), 0f
        )
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        p.textAlign = Paint.Align.CENTER
        p.color = ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_7745FF)
        p.strokeWidth = mStrokeWidth
        p.pathEffect = mDashPath
        p.style = Paint.Style.STROKE
        canvas.drawCircle((mSize / 2).toFloat(), (mSize / 2).toFloat(), (mRadius - 5.dp).toFloat(), p)
    }
}
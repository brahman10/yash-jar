package com.jar.app.feature_spin.impl.custom.component.outerRing

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

internal class DoubleCircleView @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr: Int = 0) : View(context, attrs) {

    private val outerCirclePaint = Paint().apply {
        color = Color.parseColor("#683293")
        style = Paint.Style.STROKE
        strokeWidth = 25f
    }

    private val innerCirclePaint = Paint().apply {
        color =  Color.parseColor("#461059")
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private var outerRadius = 100f
    private var innerRadius = 50f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val outerPath = Path()
    private val innerPath = Path()
    private val areaBetweenCircles = Path()
    private var bitmap: Bitmap? = null

    private fun createBitmap(width: Int, height: Int): Bitmap {
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)

        val centerX = width / 2f
        val centerY = height / 2f

        // Draw outer circle
        canvas.drawCircle(centerX, centerY, outerRadius, outerCirclePaint)

        // Create the path for the area between the circles
        outerPath.apply {
            addCircle(centerX, centerY, outerRadius, Path.Direction.CW)
        }
        innerPath.apply {
            addCircle(centerX, centerY, innerRadius, Path.Direction.CW)
        }
        areaBetweenCircles.apply {
            op(outerPath, innerPath, Path.Op.DIFFERENCE)
        }

        // Draw filled area between the circles
        paint.color = Color.parseColor("#6A3082") // Change the color to the desired solid color
        paint.style = Paint.Style.FILL
        canvas.drawPath(areaBetweenCircles, paint)

        paint.color = Color.BLACK
        canvas.drawCircle(centerX, centerY, innerRadius, paint)

        // Draw outer circle stroke
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
        canvas.drawCircle(centerX, centerY, outerRadius, paint)

        // Draw inner circle stroke
        canvas.drawCircle(centerX, centerY, innerRadius, paint)

        // Draw inner circle
        canvas.drawCircle(centerX, centerY, innerRadius, innerCirclePaint)

        return bmp
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bitmap == null) {
            bitmap = createBitmap(width, height)
        }
        bitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }
    }

    fun setRadii(innerRadius: Float, outerRadius: Float) {
        this.outerRadius = outerRadius
        this.innerRadius = innerRadius
        invalidate()
    }

}
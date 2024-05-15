package com.jar.app.core_image_picker.impl.ui.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.jar.app.base.util.dp
import com.jar.app.core_image_picker.R


class CameraSelfieOverlay @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), View.OnTouchListener {

    private val lock = Any()
    private var faceRect: RectF? = null
    private var ovalRect: RectF? = null
    private val padding = 30.dp
    private var halfOvalHeight = 200.dp
    private var shouldShowWhiteOverlay = false
    private var isValidSelfie = false

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    private val faceRectPaint = Paint().apply {
        color = ContextCompat.getColor(
            context,
            if (isValidSelfie) com.jar.app.core_ui.R.color.color_1EA787
            else com.jar.app.core_ui.R.color.color_FF4D52
        )
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 2.dp.toFloat()
    }
    private val ovalPaint: Paint = Paint().apply {
        color = ContextCompat.getColor(
            context,
            if (isValidSelfie) com.jar.app.core_ui.R.color.color_1EA787
            else com.jar.app.core_ui.R.color.color_FF4D52
        )
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 4.dp.toFloat()
    }

    fun getCropRect() = ovalRect

    private val scrimPaint: Paint = Paint().apply {
        color = ContextCompat.getColor(
            context,
            if (shouldShowWhiteOverlay) com.jar.app.core_ui.R.color.white_opacity_80
            else com.jar.app.core_ui.R.color.color_black_50_trans
        )
        style = Paint.Style.FILL
    }

    private val eraserPaint: Paint = Paint().apply {
        color = 0
        strokeWidth = ovalPaint.strokeWidth
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    fun shouldShowWhiteOverlay(shouldShowWhiteOverlay: Boolean) {
        this.shouldShowWhiteOverlay = shouldShowWhiteOverlay
        val color = ContextCompat.getColor(
            context,
            if (shouldShowWhiteOverlay) com.jar.app.core_ui.R.color.white_opacity_80
            else com.jar.app.core_ui.R.color.color_black_50_trans
        )
        scrimPaint.color = color
        invalidate()
    }

    fun setValidSelfie(isValid: Boolean) {
        this.isValidSelfie = isValid
        val color = if (isValid) ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_1EA787)
        else ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_FF4D52)
        ovalPaint.color = color
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        synchronized(lock) {
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), scrimPaint)
            val top = ((height / 2) - halfOvalHeight).toFloat()
            val bottom = ((height / 2) + halfOvalHeight).toFloat()
            if (ovalRect == null)
                ovalRect = RectF(
                    padding.toFloat(),
                    top,
                    (width - padding).toFloat(),
                    bottom
                )
            ovalRect?.let {
                eraserPaint.style = Paint.Style.FILL
                canvas.drawOval(it, eraserPaint)
                eraserPaint.style = Paint.Style.STROKE
                canvas.drawOval(it, eraserPaint)
                canvas.drawOval(it, ovalPaint)
            }
            faceRect?.let {
                canvas.drawRect(it, faceRectPaint)
            }
        }
    }

    fun drawRect(rectF: RectF) {
        faceRect = rectF
        postInvalidate()
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return false
    }

}
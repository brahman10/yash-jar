package com.jar.app.core_image_picker.impl.ui.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.jar.app.base.util.dp
import com.jar.app.core_image_picker.R

class CameraDocumentOverlay @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), View.OnTouchListener {
    private val animationDuration = 400L
    private val padding = 16.dp
    private val tapRadius = 20.dp
    private var halfBoxHeight = 100.dp
    private val cornerRadius = 8.dp.toFloat()
    private var rippleRadius = 20.dp
    private var isTapped = false
    private val fromTapCircleRadius = 40.dp
    private val toTapCircleRadius = 20.dp
    private var tapX = 0f
    private var tapY = 0f
    private var boxRect: RectF? = null
    private var previewView: PreviewView? = null
    private var isVerticalDocument = false

    init {
        setOnTouchListener(this)
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    private val boxPaint: Paint = Paint().apply {
        color = ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_626262)
        style = Paint.Style.STROKE
        strokeWidth = 1.dp.toFloat()
    }
    private val tapRipplePaint: Paint = Paint().apply {
        color = ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_B1A7CE)
        style = Paint.Style.STROKE
        strokeWidth = 1.dp.toFloat()
    }
    private val tapCirclePaint: Paint = Paint().apply {
        color = ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_B1A7CE)
        style = Paint.Style.FILL
    }

    private val scrimPaint: Paint = Paint().apply {
        color = ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_black_50_trans)
        style = Paint.Style.FILL
    }

    private val eraserPaint: Paint = Paint().apply {
        color = 0
        strokeWidth = boxPaint.strokeWidth
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    fun setVerticalDocument(isVerticalDocument: Boolean) {
        this.isVerticalDocument = isVerticalDocument
        halfBoxHeight = if (isVerticalDocument) 156.dp else 100.dp
        invalidate()
    }

    fun getCropBoxRect() = boxRect

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), scrimPaint)
        val top = ((height / 2) - halfBoxHeight).toFloat()
        val bottom = ((height / 2) + halfBoxHeight).toFloat()
        if (boxRect == null)
            boxRect = RectF(
                padding.toFloat(),
                top,
                (width - padding).toFloat(),
                bottom
            )
        boxRect?.let {
            eraserPaint.style = Paint.Style.FILL
            canvas.drawRoundRect(it, cornerRadius, cornerRadius, eraserPaint)
            eraserPaint.style = Paint.Style.STROKE
            canvas.drawRoundRect(it, cornerRadius, cornerRadius, eraserPaint)
            canvas.drawRoundRect(it, cornerRadius, cornerRadius, boxPaint)
      }
        if (isTapped) {
            canvas.drawCircle(tapX, tapY, rippleRadius.toFloat(), tapRipplePaint)
            canvas.drawCircle(tapX, tapY, tapRadius.toFloat(), tapCirclePaint)
        }
    }

    private fun animateRippleCircle() {
        val valueAnimator = ValueAnimator.ofInt(fromTapCircleRadius, toTapCircleRadius).apply {
            duration = animationDuration
            interpolator = AccelerateInterpolator()
        }
        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Int
            rippleRadius = value
            if (value == toTapCircleRadius)
                isTapped = false
            invalidate()
        }
        valueAnimator.start()

    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        previewView?.dispatchTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                tapX = event.x
                tapY = event.y
                return true
            }
            MotionEvent.ACTION_UP -> {
                isTapped = true
//                animateRippleCircle()
            }
        }
        return super.onTouchEvent(event)
    }

    fun setPreview(previewView: PreviewView?) {
        this.previewView = previewView
    }
}
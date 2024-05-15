package com.jar.app.core_image_picker.impl.ui.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.media.MediaActionSound
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.jar.app.base.util.dp

class CameraCaptureButtonView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), View.OnTouchListener {
    private val animationDuration = 200L
    private val highlightedColor: Int =
        ContextCompat.getColor(context, com.jar.app.core_ui.R.color.white)
    private val disabledColor: Int =
        ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_626262)
    private val viewSize = 70.dp
    private val gapBetween = 8.dp
    private val outerRingRadius = 32.dp
    private val clickedCircleRadius = 15.dp
    private val outerStrokeWidth = 4.dp
    private val normalRadius = 26.dp
    private val smallRadius = 20.dp

    private val innerCirclePaint: Paint
    private val clickedCirclePaint: Paint
    private val outerRingPaint: Paint
    private var innerCircleRadius = normalRadius
    private var isClicked = false
    private var isDisabled = false


    private var onCaptureListener: OnClickListener? = null

    init {
        innerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (isDisabled) disabledColor else highlightedColor
            style = Paint.Style.FILL
        }
        clickedCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (isDisabled) disabledColor else highlightedColor
            style = Paint.Style.FILL
        }
        outerRingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (isDisabled) disabledColor else highlightedColor
            strokeWidth = outerStrokeWidth.toFloat()
            style = Paint.Style.STROKE
        }
        setOnTouchListener(this)
    }

    fun setCaptureDisabled(isDisabled: Boolean) {
        this.isDisabled = isDisabled
        innerCirclePaint.color = if (isDisabled) disabledColor else highlightedColor
        outerRingPaint.color = if (isDisabled) disabledColor else highlightedColor
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2.toFloat()
        val centerY = height / 2.toFloat()
        canvas.drawCircle(
            centerX,
            centerY,
            innerCircleRadius.toFloat(),
            innerCirclePaint
        )
        canvas.drawCircle(
            centerX,
            centerY,
            outerRingRadius.toFloat(),
            outerRingPaint
        )
        if (isClicked)
            canvas.drawCircle(
                centerX,
                centerY,
                clickedCircleRadius.toFloat(),
                clickedCirclePaint
            )
    }

    override fun setOnClickListener(l: OnClickListener?) {
        this.onCaptureListener = l
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(viewSize, viewSize)
    }

    private fun onCaptureClicked() {
        val sound = MediaActionSound()
        sound.play(MediaActionSound.SHUTTER_CLICK)
        onCaptureListener?.onClick(this)
        isDisabled = true
        this.postDelayed({
            isDisabled = false
        }, 1000L) //to make debounce capture button
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (isDisabled) return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isClicked = true
                animateInnerCircle(normalRadius, smallRadius)
            }

            MotionEvent.ACTION_UP -> {
                isClicked = false
                animateInnerCircle(smallRadius, normalRadius)
                onCaptureClicked()
            }
        }
        return true
    }

    private fun animateInnerCircle(from: Int, to: Int) {
        val valueAnimator = ValueAnimator.ofInt(from, to).apply {
            duration = animationDuration
        }
        valueAnimator.addUpdateListener {
            innerCircleRadius = it.animatedValue as Int
            invalidate()
        }
        valueAnimator.start()

    }

}
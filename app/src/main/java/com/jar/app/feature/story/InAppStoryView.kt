package com.jar.app.feature.story

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.jar.app.R

class InAppStoryView @JvmOverloads constructor(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {

    init {
        setImageResource(R.drawable.jar_story_icon)
        setBackgroundColor(Color.TRANSPARENT)
        scaleType = ScaleType.CENTER_INSIDE
        if (!isInEditMode) {
            setLayerType(LAYER_TYPE_HARDWARE, null)
        }
    }

    var isDisable = true
    private val listOfViewSegment: MutableList<Int> = mutableListOf()

    private val paintBorder = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 5f
        color = Color.BLACK
        strokeCap = Paint.Cap.ROUND
    }

    private val path = Path()
    private lateinit var shader: LinearGradient
    private lateinit var disableShader: LinearGradient

    private var gap = 5f
    private var dotCount: Int = 12

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.InAppStoryView)
        gap = attributes.getDimension(R.styleable.InAppStoryView_borderGap, gap * resources.displayMetrics.density)
        dotCount = attributes.getInt(R.styleable.InAppStoryView_dotCount, dotCount)
        attributes.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        shader = LinearGradient(0f, 0f, w.toFloat(), h.toFloat(), Color.parseColor("#1EA787"), Color.parseColor("#1EA787"), Shader.TileMode.CLAMP)
        disableShader = LinearGradient(0f, 0f, w.toFloat(), h.toFloat(), Color.parseColor("#3F3954"), Color.parseColor("#3F3954"), Shader.TileMode.CLAMP)
    }

    override fun onDraw(canvas: Canvas) {
        val width = width.toFloat()
        val center = width / 2
        val imageRadius = (width - 2 * gap - paintBorder.strokeWidth) / 2
        val borderRadius = imageRadius + gap

        // Drawing the clipped circle for the image
        canvas.save()
        path.reset()
        path.addCircle(center, center, imageRadius, Path.Direction.CW)
        canvas.clipPath(path)
        super.onDraw(canvas)
        canvas.restore()

        val sep = when (dotCount) {
            1 -> 1f
            2 -> 0.9f
            3 -> 0.8f
            else -> 0.6f
        }

        val baseArcLength = 360f / dotCount
        val segmentLength = baseArcLength * sep
        val offsetAngle = (baseArcLength - segmentLength) / 2
        val separation = baseArcLength * 0.1f

        if (isDisable) {
            paintBorder.shader = disableShader
            canvas.drawCircle(center, center, borderRadius, paintBorder)
        } else {
            drawSegments(canvas, center, borderRadius, baseArcLength, segmentLength, offsetAngle, separation)
        }
    }

    private fun drawSegments(canvas: Canvas, center: Float, borderRadius: Float, baseArcLength: Float, segmentLength: Float, offsetAngle: Float, separation: Float) {
        // Draw active segments
        paintBorder.shader = shader
        drawSpecificSegments(canvas, center, borderRadius, baseArcLength, segmentLength, offsetAngle, separation, false)

        // Draw disabled segments
        paintBorder.shader = disableShader
        drawSpecificSegments(canvas, center, borderRadius, baseArcLength, segmentLength, offsetAngle, separation, true)
    }

    private fun drawSpecificSegments(canvas: Canvas, center: Float, borderRadius: Float, baseArcLength: Float, segmentLength: Float, offsetAngle: Float, separation: Float, isDisabled: Boolean) {
        for (i in 0 until dotCount) {
            val startAngle = (i * baseArcLength + offsetAngle) - 45
            val condition = if (isDisabled) listOfViewSegment.contains(i) else !listOfViewSegment.contains(i)

            if (condition) {
                canvas.drawArc(
                    center - borderRadius, center - borderRadius,
                    center + borderRadius, center + borderRadius,
                    startAngle,
                    segmentLength,
                    false, paintBorder
                )
            }
        }
    }

    fun updateDots(number: Int) {
        if (dotCount != number) {
            dotCount = number
            invalidate()
        }
    }

    fun setSegmentColorRangeAndIsDisable(segment: Int, isDisable: Boolean, dotCount: Int) {
        var isChanged = false
        if (this.isDisable != isDisable || this.dotCount != dotCount) {
            this.isDisable = isDisable
            this.dotCount = dotCount
            isChanged = true
        }
        listOfViewSegment.clear()
        for (i in 0 until segment) {
            listOfViewSegment.add(i)
        }
        if (isChanged) {
            invalidate()
        }
    }

    fun setIsDisable(isDisable: Boolean) {
        if (this.isDisable != isDisable) {
            this.isDisable = isDisable
            invalidate()
        }
    }
}
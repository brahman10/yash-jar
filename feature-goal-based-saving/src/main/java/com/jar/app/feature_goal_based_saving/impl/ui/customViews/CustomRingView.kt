package com.jar.app.feature_goal_based_saving.impl.ui.customViews


import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.jar.app.feature_goal_based_saving.R
import com.jar.app.feature_goal_based_saving.impl.ui.manage.GbsProgressBarCallbackListener
import kotlin.math.cos
import kotlin.math.sin

internal class CustomRingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    private var callbackListener: GbsProgressBarCallbackListener? = null

    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.TRANSPARENT // Or any other color
    }


    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#3C3357")
        strokeWidth = 5f
    }

    private val traceCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#FCE874")
    }

    private val traceTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 30f
        color = Color.parseColor("#1A162A")
        typeface = Typeface.DEFAULT_BOLD
    }

    private val traceLinePain = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 10f
        color = Color.parseColor("#FCE874")
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#ACA1D3")
        textSize = 30f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT
    }

    private val ringRect = RectF()
    private val lineRect = RectF()
    private val linePath = Path()
    private val porterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    private var ringThickness: Float = 80f
    private var lineOffset: Float = 30f
    private var lineDrawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.winning_cup)
    private val ovalSize = 90f
    private val ovalOffset = 0f // Adjusted offset towards the left by 20 pixels
    private var percentage: Float = 59f
    private val fillPaint = Paint(ringPaint)
    private val textBgPaint = Paint().apply {
        color = Color.parseColor("#3C3357")
    }
    private val nonFillPaint = Paint(ringPaint).apply {
        color = Color.parseColor("#3C3357") // Non-fill color
    }

    private fun getFillColor(percentage: Float): Int {
        return when {
            percentage <= 0f -> Color.parseColor("#774AF3")
            percentage <= 0.25f -> Color.parseColor("#774AF3")
            percentage <= 0.50f -> Color.parseColor("#774AF3")
            percentage <= 0.75f -> Color.parseColor("#774AF3")
            else -> Color.parseColor("#774AF3")
        }
    }

    private fun getOvalColor(textPercentage: Float, percentage: Float): Int {
        return if (percentage >= textPercentage) {
            Color.parseColor("#FCE874")
        } else {
            Color.parseColor("#3C3357")
        }
    }

    private fun getTextColor(textPercentage: Float, percentage: Float): Int {
        return if (percentage >= textPercentage) {
            Color.parseColor("#1A162A")
        } else {
            Color.parseColor("#ACA1D3")
        }
    }

    private fun getDrawable(resourceId: Int) {
        lineDrawable = ContextCompat.getDrawable(context, resourceId)
    }

    var topText: String = ""
    var bottomText: String = ""

    private val textPaintTop = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 28f
        color = Color.WHITE
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val textPaintBottom = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 16f
        color = Color.parseColor("#ACA1D3")
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

    }
    // Declare lineFillPaint and lineNonFillPaint variables
    private val lineFillPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
        color = Color.parseColor("#764AF5") // Fill color
    }

    private val lineNonFillPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
        color = Color.parseColor("#3C3357") // Non-fill color
    }
    private val textBound = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val halfStrokeWidth = linePaint.strokeWidth / 2
        val adjustedPadding = paddingLeft + ringThickness + lineOffset
        val width = width - adjustedPadding - paddingRight
        val cx = (width + adjustedPadding) / 2

        val paddintop = 50f
        val height = height - paddingBottom - paddintop
        val radius = (width.coerceAtMost(height.toFloat()) / 2) - ringThickness / 2 - lineOffset - halfStrokeWidth
        val cy = height / 2f + paddintop

        ringRect.set(
            cx - radius,
            cy - radius,
            cx + radius,
            cy + radius
        )

        lineRect.set(
            cx - radius - ringThickness / 2 - halfStrokeWidth - lineOffset - halfStrokeWidth,
            cy - radius - ringThickness / 2 - halfStrokeWidth - lineOffset - halfStrokeWidth,
            cx + radius + ringThickness / 2 + halfStrokeWidth + lineOffset + halfStrokeWidth,
            cy + radius + ringThickness / 2 + halfStrokeWidth + lineOffset + halfStrokeWidth
        )

        val sweepAngle = 68f
        val segmentGap = 4f

        val fullSegments = (percentage / 25).toInt()
        val nextSegment = (percentage % 25) / 25

        var startAngleLine = 0f
        var sweepAngleLine = 0f

        canvas.drawPath(linePath, linePaint)

        // Draw the outer line
        if (percentage >= 0f) {
            if (percentage == 100f) {
                canvas.drawPath(linePath, lineFillPaint)
            } else {
                val startAngleLine = (135 + 0 * (sweepAngle + segmentGap))
                val sweepAngleLine = (135 + 3 * (sweepAngle + segmentGap)) + sweepAngle - startAngleLine

                val fillSweepAngle = (sweepAngleLine * (percentage / 100)).toFloat()
                val nonFillSweepAngle = sweepAngleLine - fillSweepAngle

                val linePathFill = Path()
                linePathFill.addArc(lineRect, startAngleLine, fillSweepAngle)
                canvas.drawPath(linePathFill, lineFillPaint)

                val linePathNonFill = Path()
                linePathNonFill.addArc(lineRect, startAngleLine + fillSweepAngle, nonFillSweepAngle)
                canvas.drawPath(linePathNonFill, lineNonFillPaint)
            }
        }

        for (i in 0 until 4) {
            val startAngle = (135 + i * (sweepAngle + segmentGap))
            if (i == 0) {
                startAngleLine = startAngle
            }
            if (i == 3) {
                sweepAngleLine = startAngle + sweepAngle - startAngleLine
            }
        }

        val midRadius = radius - ringThickness / 2
        val startRad = Math.toRadians(startAngleLine.toDouble())
        val endRad = Math.toRadians((startAngleLine + sweepAngleLine).toDouble())

        val startX = (cx + midRadius * cos(startRad)).toFloat() - halfStrokeWidth
        val startY = (cy + midRadius * sin(startRad)).toFloat() - halfStrokeWidth
        val endX = (cx + midRadius * cos(endRad)).toFloat() + halfStrokeWidth - 3
        val endY = (cy + midRadius * sin(endRad)).toFloat() + halfStrokeWidth - 1

        val outerLineColor = getFillColor(percentage)
        linePaint.color = outerLineColor

        if (percentage == 0f) {
            canvas.drawCircle(startX, startY, ringThickness / 2, nonFillPaint)
        } else {
            canvas.drawCircle(startX, startY, ringThickness / 2, fillPaint)
        }

        if (percentage < 100f) {
            canvas.drawCircle(endX, endY, ringThickness / 2, nonFillPaint)
        } else {
            canvas.drawCircle(endX, endY, ringThickness / 2, fillPaint)
        }

        for (i in 0 until 4) {
            val startAngle = (135 + i * (sweepAngle + segmentGap))
            fillPaint.color = getFillColor(i / 4f)
            if (i < fullSegments) {
                canvas.drawArc(
                    ringRect,
                    startAngle,
                    sweepAngle,
                    true,
                    fillPaint
                )
            } else if (i == fullSegments) {
                canvas.drawArc(
                    ringRect,
                    startAngle,
                    sweepAngle * nextSegment,
                    true,
                    fillPaint
                )
                canvas.drawArc(
                    ringRect,
                    startAngle + sweepAngle * nextSegment,
                    sweepAngle * (1 - nextSegment),
                    true,
                    nonFillPaint
                )
            } else {
                canvas.drawArc(
                    ringRect,
                    startAngle,
                    sweepAngle,
                    true,
                    nonFillPaint
                )
            }

            if (i == 0) {
                startAngleLine = startAngle
            }

            if (i == 3) {
                sweepAngleLine = startAngle + sweepAngle - startAngleLine
            }

            val text = "${i * 25}%"
            val textWidth = textPaint.measureText(text)
            val textHeight = textPaint.fontMetrics.bottom - textPaint.fontMetrics.top

            val textPathMeasure = PathMeasure(linePath, false)
            val segmentLength = (textPathMeasure.length - segmentGap - ovalSize / 2) / 4

            val textOnPathOffset = segmentLength * i

            val textPath = Path()
            textPathMeasure.getSegment(
                textOnPathOffset - ovalOffset,
                textOnPathOffset + textWidth - ovalOffset,
                textPath,
                true
            )

            val textBounds = RectF()
            textPath.computeBounds(textBounds, true)

            if (i * 25 < percentage) {
                val drawableBounds = Rect(
                    (textBounds.centerX() - ovalSize / 2).toInt(),
                    (textBounds.centerY() - ovalSize / 2).toInt(),
                    (textBounds.centerX() + ovalSize / 2).toInt(),
                    (textBounds.centerY() + ovalSize / 2).toInt()
                )

                val inset = 0
                if (i != 0) {
                    lineDrawable?.bounds = drawableBounds
                    lineDrawable?.bounds?.inset(inset, inset)
                    lineDrawable?.draw(canvas)
                }
            } else {
                val textBackgroundRect = RectF(
                    textBounds.centerX() - ovalSize / 2,
                    textBounds.centerY() - ovalSize / 2,
                    textBounds.centerX() + ovalSize / 2,
                    textBounds.centerY() + ovalSize / 2
                )

                val textBaselineOffset = (textHeight / 2) - textPaint.fontMetrics.bottom

                canvas.drawOval(textBackgroundRect, textBgPaint.apply {
                    color = getOvalColor((i * 25).toFloat(), percentage)
                })

                canvas.save()  // Save the current state of the canvas
                canvas.rotate(6f, textBounds.centerX(), textBounds.centerY()) // Rotate the canvas 270 degrees
                canvas.drawText(text, textBounds.centerX(), textBounds.centerY() + textBaselineOffset, textPaint.apply {
                    color = getTextColor((i * 25).toFloat(), percentage)
                })
                canvas.restore()  // Restore the state of the canvas
            }
        }

        linePath.reset()
        linePath.addArc(lineRect, startAngleLine, sweepAngleLine)


        if (percentage > 0f) {
            val outerLineRadius = radius + ringThickness / 2 + lineOffset
            val tracingRad = Math.toRadians((startAngleLine + sweepAngleLine * (percentage / 100)).toDouble())
            val angle = 0f // Set the angle to 0 degrees to make the text vertically downward
            val tracingX = (cx + outerLineRadius * cos(tracingRad)).toFloat()
            val tracingY = (cy + outerLineRadius * sin(tracingRad)).toFloat()

            val percentageText = "${percentage.toInt()}%"
            val fm = textPaint.fontMetrics
            val textHeight = fm.descent - fm.ascent
            val textWidth = traceTextPaint.measureText(percentageText)
            val xPos = tracingX - textWidth / 2
            val yPos = tracingY + textHeight / 2 - fm.descent

            canvas.drawCircle(tracingX, tracingY, ovalSize / 2, traceCirclePaint)
            canvas.drawLine(cx, cy, tracingX, tracingY, traceLinePain)
            canvas.save() // Save the current canvas state
            canvas.rotate(angle, tracingX, tracingY) // Rotate the canvas around the center of the trace circle

            canvas.drawText(percentageText, xPos, yPos, traceTextPaint)

            canvas.restore()
        }

        ringPaint.xfermode = porterDuffXfermode
        canvas.drawCircle(cx, cy, radius - ringThickness, ringPaint)
        ringPaint.xfermode = null

        val ringMidPoint = height / 2f

        textPaintTop.textSize = 35f
        textPaintTop.color = Color.WHITE
        textPaintTop.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        val topTextHeight = textPaintTop.descent() - textPaintTop.ascent()
        val topTextOffset = (ringThickness - topTextHeight) / 2f

        canvas.drawText(
            topText,
            (width / 2f) + ringThickness / 2f,
            ringMidPoint - topTextOffset - topTextHeight / 2f,
            textPaintTop
        )

        val bottomTextHeight = textPaintBottom.descent() - textPaintBottom.ascent()
        val bottomTextOffset = (ringThickness - bottomTextHeight) / 2f

        canvas.drawText(
            bottomText,
            (width / 2f) + ringThickness / 2f,
            ringMidPoint + bottomTextOffset + bottomTextHeight / 2f,
            textPaintBottom
        )
    }




    fun setPercentage(value: Float) {
        assert(value <= 100)
        percentage = value
        invalidate()
    }

    fun topText(text: String) {
        topText = text;
        invalidate()
    }

    fun setBottonText(text: String) {
        bottomText = text
        invalidate()
    }


    fun animateFillToPercentage(startPercentage: Int = 0, targetPercentage: Int = 0, durationMillis: Long = 2000L) {
        val animator = ValueAnimator.ofFloat(startPercentage.toFloat(), targetPercentage.toFloat()).apply {
            duration = durationMillis
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                percentage = animation.animatedValue as Float
                callbackListener?.onProgressChange(percentage)
                invalidate() // trigger a redraw
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {
                }

                override fun onAnimationEnd(p0: Animator) {
                    callbackListener?.onAnimationEnded()
                }

                override fun onAnimationCancel(p0: Animator) {
                }

                override fun onAnimationRepeat(p0: Animator) {
                }

            })
        }
        animator.start()
    }

    fun setCallbackListener(callbackListener: GbsProgressBarCallbackListener) {
        this.callbackListener = callbackListener
    }
}

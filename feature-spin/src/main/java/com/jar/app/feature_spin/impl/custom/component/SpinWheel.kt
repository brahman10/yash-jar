package com.jar.app.feature_spin.impl.custom.component

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.res.ResourcesCompat
import com.jar.app.base.util.dp
import com.jar.app.feature_spin.R
import com.jar.app.feature_spin.impl.custom.interpolators.spinRotationInterpolator
import com.jar.app.feature_spin.impl.custom.listeners.SpinWheelListener
import com.jar.app.feature_spin.impl.custom.util.JACKPOT
import com.jar.app.feature_spin.shared.domain.model.Option
import com.jar.app.feature_spin.impl.data.models.SpinsContextFlowType
import java.lang.Float.max
import java.lang.Float.min

internal class SpinWheel(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var flowTypeContext: SpinsContextFlowType = SpinsContextFlowType.SPINS
    private var nWinningValues: List<com.jar.app.feature_spin.shared.domain.model.Option>? = null
    private var nSegments = 0 // default number of segments
    private var currentSegment = -1 // default current segment (no segment)
    private var anim: ValueAnimator? = null // animation object
    private val paddingPercentage = 6 // Set the padding percentage value you want here
    private val segmentPaints =
        mutableListOf<Pair<Paint, Paint>>()// list of paints for each segment
    private var spinWheelListener: SpinWheelListener? = null
    private val textPaint: Paint by lazy {
        Paint()
    }
    private val textStrokePaint = Paint()
    private var ohNoRectF: RectF? = null
    private val path = Path()
    private val jackpotMatrix by lazy {
        Matrix()
    }
    private val labelRadius: Float by lazy {
        val centerX = width / 2f
        val centerY = height / 2f
        val padding = min(centerX, centerY) * (paddingPercentage / 100f)
        (min(centerX, centerY)) - padding - 150f
    }
    private var jackpotDrawable: Drawable? = null
    private var jackpotBitmap: Bitmap? = null

    private val ohNoPaint by lazy {
        Pair(Paint().apply {
            textSize = 60f
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            style = Paint.Style.FILL
            strokeCap = Paint.Cap.ROUND
        }, Paint().apply {
            textSize = 65f
            color = Color.parseColor("#DA3859")
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            strokeWidth = 2f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        })

    }
    private var numberOfFreeRotation = 4

    companion object {
        const val TAG = "SpinWheel"
    }

    fun addSpinListener(spinWheelListener: SpinWheelListener) {
        this.spinWheelListener = spinWheelListener
    }

    fun setNSegments(list: List<Option>, flowTypeContext: SpinsContextFlowType) {
        this.flowTypeContext = flowTypeContext
        nSegments = list.size
        nWinningValues = list
        segmentPaints.clear()
        for (i in 0 until nSegments) {
            segmentPaints.add(Pair(Paint().apply {
                color = Color.parseColor(list[i].colorCode)
                style = Paint.Style.FILL
                isAntiAlias = true
            }, Paint().apply {
                color = Color.parseColor(list[i].strokeColor)
                strokeWidth = 5f.dp
                style = Paint.Style.STROKE
                isAntiAlias = true
            }))
        }
        invalidate()
    }

    fun rotateAndStop(segment: Int, duration: Long) {
        if (segment < 0 || segment >= nSegments) return
        // calculate the final rotation angle based on the desired segment index
        val currentRotation = this.rotation
        val targetRotation =
            (360f * numberOfFreeRotation + 360f / nSegments * (nSegments - segment))
        val finalRotation = targetRotation - (currentRotation % 360f)
        // create a new animation object
        anim?.cancel()
        anim = ValueAnimator.ofFloat(currentRotation, currentRotation + finalRotation) // Start from the previous position
        anim?.duration = duration
        anim?.interpolator = spinRotationInterpolator
        anim?.addUpdateListener { valueAnimator ->
            val rotation = valueAnimator.animatedValue as Float
            this.rotation = rotation
            currentSegment = ((rotation / 360f * nSegments).toInt() + nSegments) % nSegments
        }
        anim?.doOnEnd {
            spinWheelListener?.onSpinComplete()
        }
        anim?.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val padding = min(centerX, centerY) * (paddingPercentage / 100f)

        val radius = (min(centerX, centerY)) - padding

        val angle = 360f / nSegments
        val halfOfAngle = angle / 2f

        var startAngle: Float

        for (i in 0 until nSegments) {
            startAngle = (i * angle) - (90f + halfOfAngle)

            val textangle = (i * angle)

            val winningValue = nWinningValues?.getOrNull(i)?.value

            val showQuestionMarkString = nWinningValues?.getOrNull(i)?.showValue

            if (showQuestionMarkString != null || winningValue != JACKPOT) {
                canvas.drawArc(
                    centerX - radius,
                    centerY - radius,
                    centerX + radius,
                    centerY + radius,
                    startAngle + 2,
                    angle - 2,
                    true,
                    segmentPaints[i].first
                )

                canvas.drawArc(
                    centerX - radius,
                    centerY - radius,
                    centerX + radius,
                    centerY + radius,
                    startAngle + 2,
                    angle - 2,
                    true,
                    segmentPaints[i].second
                )
            } else {
                if (jackpotDrawable == null) {
                    jackpotDrawable = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.jackpot_bg,
                        null
                    )
                }
                if (jackpotBitmap == null){
                    jackpotBitmap = drawableToBitmap(jackpotDrawable!!)
                }

                // Calculate the scaling factor based on the size of the area you want to fill.
                val widthScaleFactor = (radius) / (jackpotBitmap?.width?.toFloat() ?: 1f)
                val heightScaleFactor = (radius) / (jackpotBitmap?.height?.toFloat() ?: 1f )

                // Create the shader with the scaling factor applied
                val shader = BitmapShader(
                    jackpotBitmap!!,
                    Shader.TileMode.CLAMP,
                    Shader.TileMode.CLAMP
                )

                // Get the canvas size
                val canvasWidth = canvas.width.toFloat()
                val canvasHeight = canvas.height.toFloat()

                // Calculate the center coordinates of the canvas
                val canvasCenterX = canvasWidth / 2
                val canvasCenterY = canvasHeight / 2

                // Apply the scaling factors and translation to the shader's matrix.
                shader.setLocalMatrix(
                    jackpotMatrix.apply {
                        setScale(widthScaleFactor, heightScaleFactor)
                        postTranslate(canvasCenterX - radius, canvasCenterY - radius)
                    }
                )

                canvas.drawArc(
                    centerX - radius,
                    centerY - radius,
                    centerX + radius,
                    centerY + radius,
                    startAngle + 2,
                    angle -2,
                    true,
                    segmentPaints[i].first.apply {
                        this.shader = shader
                    }
                )

                canvas.drawArc(
                    centerX - radius,
                    centerY - radius,
                    centerX + radius,
                    centerY + radius,
                    startAngle + 2,
                    angle - 2,
                    true,
                    segmentPaints[i].second
                )
            }

            // calculate the position of the text label
            val textAngle = startAngle + angle / 2f
            val textX =
                centerX + labelRadius * kotlin.math.cos(Math.toRadians(textAngle.toDouble()))
                    .toFloat()
            val textY =
                centerY + labelRadius * kotlin.math.sin(Math.toRadians(textAngle.toDouble()))
                    .toFloat()

            // rotate the canvas to align the text label with the top of the arc
            canvas.rotate((textangle), textX, textY)

            val textLabelForSpinSegments = nWinningValues?.getOrNull(i)?.showValue ?: kotlin.run {
                nWinningValues?.getOrNull(i)?.value
            }

            // draw the text label
            val labelText = if (flowTypeContext == SpinsContextFlowType.SPINS) "${textLabelForSpinSegments ?: ""}" else "?"
            textPaint.apply {
                color = Color.WHITE
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
                style = Paint.Style.FILL
            }

            textStrokePaint.apply {
                color = Color.parseColor(nWinningValues?.getOrNull(i)?.strokeColor)
                style = Paint.Style.STROKE
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
                strokeWidth = 3f
                typeface = Typeface.DEFAULT_BOLD
                style = Paint.Style.STROKE
                strokeCap = Paint.Cap.ROUND
            }

            setTextPaintForArc(
                arcRadius = radius,
                text = labelText,
                minTextSize = 5f.dp,
                maxTextSize = 100f.dp,
                paint = textPaint,
                textStrokePaint
            )

            if ((nWinningValues?.getOrNull(i)?.value ?: -1) == 0) {
                // Save the current canvas state
                canvas.save()

                // Translate the canvas to the specified start position
                canvas.translate(textX, textY)

                // Calculate the arc's bounding rectangle
                if (ohNoRectF == null) {
                    ohNoRectF = RectF(
                        -radius, -0f, radius , 0f
                    )
                }

                // Create the arc-shaped path
                path.reset()
                path.addArc(ohNoRectF!!, startAngle, angle)
                val ohNoSegmentText = nWinningValues?.getOrNull(i)?.showValue ?: kotlin.run {
                    "Oh no!"
                }
                if (nWinningValues?.getOrNull(i)?.showValue != null) {
                    setTextPaintForArc(
                        arcRadius = radius,
                        text = ohNoSegmentText,
                        minTextSize = 5f.dp,
                        maxTextSize = 100f.dp,
                        paint = textPaint,
                        ohNoPaint.first
                    )
                }
                // Draw the text along the path
                canvas.drawTextOnPath(ohNoSegmentText, path, 0f, 0f, ohNoPaint.first)
                //canvas.drawTextOnPath("Oh no!", path, 0f, 0f, ohNoPaint.second)

                // Restore the canvas state
                canvas.restore()
            } else if (showQuestionMarkString != null || (nWinningValues?.get(i)?.value) != JACKPOT) {
                // draw the text label
                canvas.drawText(labelText, textX, textY, textPaint)
              //  canvas.drawText(labelText, textX, textY, textStrokePaint)
            }
            // rotate the canvas back to its original position
            canvas.rotate(-textangle, textX, textY)
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun setTextPaintForArc(
        arcRadius: Float,
        text: String,
        minTextSize: Float,
        maxTextSize: Float,
        paint: Paint,
        textStrokePaint: Paint? = null
    ): Paint {
        // calculate the circumference of the arc
        val arcCircumference = arcRadius * Math.PI * 2

        // calculate the text size based on the arc circumference and text length
        val textLength = text.length
        val scaleFactor = 0.035f // adjust this value to achieve the desired scaling
        val textSize = (arcCircumference * scaleFactor) / textLength
        val scaledTextSize = max(
            minTextSize,
            min(maxTextSize, textSize.toFloat())
        ) // ensure text size stays within min/max bounds

        paint.textSize = scaledTextSize
        textStrokePaint?.textSize = scaledTextSize

        return paint
    }
}

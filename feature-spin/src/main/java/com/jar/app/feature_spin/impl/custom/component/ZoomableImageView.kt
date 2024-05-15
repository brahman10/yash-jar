package com.jar.app.feature_spin.impl.custom.component

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet

internal class ZoomableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {

    private val zoomMatrix: Matrix = Matrix()

    private val zoomFactor = 1.1f
    private val animationDuration = 500L // Duration in milliseconds

    init {
        scaleType = ScaleType.MATRIX
    }

    fun zoomInZoomOut() {
        smoothZoomIn()
        postDelayed({
            smoothZoomOut()
        }, animationDuration)
    }

    private fun smoothZoomIn() {
        smoothZoom(zoomFactor)
    }

    private fun smoothZoomOut() {
        smoothZoom(1f / zoomFactor)
    }

    private fun smoothZoom(targetZoomFactor: Float) {
        val startMatrixValues = FloatArray(9)
        val endMatrixValues = FloatArray(9)
        zoomMatrix.getValues(startMatrixValues)

        val endMatrix = Matrix(zoomMatrix)
        endMatrix.postScale(targetZoomFactor, targetZoomFactor, width / 2f, height / 2f)
        endMatrix.getValues(endMatrixValues)

        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.addUpdateListener { animation ->
            val interpolatedValue = animation.animatedValue as Float

            val currentMatrixValues = FloatArray(9)
            for (i in 0..8) {
                currentMatrixValues[i] =
                    startMatrixValues[i] + (endMatrixValues[i] - startMatrixValues[i]) * interpolatedValue
            }

            zoomMatrix.setValues(currentMatrixValues)
            imageMatrix = zoomMatrix
        }

        valueAnimator.duration = animationDuration
        valueAnimator.start()
    }

    fun zoomIn(zoomFactor: Float) {
        zoomMatrix.postScale(zoomFactor, zoomFactor, width / 2f, height / 2f)
        imageMatrix = zoomMatrix
    }

    fun resetZoom() {
        zoomMatrix.reset()
        imageMatrix = zoomMatrix
    }

}
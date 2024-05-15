package com.jar.app.core_ui.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.widget.AppCompatImageView

class RotatingImageView(context: Context, attrs: AttributeSet? = null) :
    AppCompatImageView(context, attrs) {

    companion object {
        const val ROTATION_ANIMATION_DURATION = 300L
        const val REPEAT_COUNT = 0
    }

    private val accelerateInterpolator: AccelerateInterpolator by lazy {
        AccelerateInterpolator()
    }
    private val rotateClockwise: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(this, View.ROTATION, 0f, 180f).apply {
            duration = ROTATION_ANIMATION_DURATION
            repeatCount = REPEAT_COUNT
            interpolator = accelerateInterpolator
        }
    }
    private val rotateAntiClockwise: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(this, View.ROTATION, 180f, 0f).apply {
            duration = ROTATION_ANIMATION_DURATION
            repeatCount = 0
            interpolator = accelerateInterpolator
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    fun rotateClockWise() {
        rotateClockwise.start()
    }

    fun rotateAntiClockWise() {
        rotateAntiClockwise.start()
    }
}
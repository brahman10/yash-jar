package com.jar.app.feature_spin.impl.custom.listeners

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import com.jar.app.base.util.dp
import com.jar.app.feature_spin.R

internal class DragDropTouchListener(
    private val onYChange: (dy: Float) -> Unit = {},
    private val onStartSpin: () -> Unit = {},
    private val onCancel: () -> Unit = {},
    private val onCrossThreshold: () -> Unit = {},
    private val onPause: () -> Unit = {},
    private val onPlayLiverUpSound: () -> Unit = {},
    private val onPlayVibration: () -> Unit = {}
) : View.OnTouchListener {
    private val minHeight: Float = 0f
    var activeCoinHeight: Float = 70f.dp
    var maxDepth: Float = 0f.dp
    get() {
        return field - activeCoinHeight
    }
    private val pullDownThreshold: Float
        get() {
        return (maxDepth/2)
    }
    private var lastY: Float = 0f
    private var dragView: View? = null
    private val pauseThreshold: Long = 1000L // Pause threshold in milliseconds
    private var isVibrationPlayed = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        dragView = view
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = event.rawY
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val dy = event.rawY - lastY
                lastY = event.rawY
                val newTranslationY = (view?.translationY ?: 0f) + dy
                view?.translationY = when {
                    newTranslationY > maxDepth -> maxDepth
                    newTranslationY < minHeight -> minHeight
                    else -> newTranslationY
                }

                if (newTranslationY <= pullDownThreshold) {
                    onYChange.invoke(newTranslationY)
                    isVibrationPlayed = false
                } else {
                    onCrossThreshold.invoke()
                    if ((view?.translationY ?: 0f) >= maxDepth) {
                        if (!isVibrationPlayed) {
                            isVibrationPlayed = true
                            onPlayVibration.invoke()
                            onPause.invoke()
                        }
                    }
                }
                return true
            }

            MotionEvent.ACTION_UP -> {
                val dy = event.rawY - lastY
                lastY = event.rawY
                val newTranslationY = (view?.translationY ?: 0f) + dy
                view?.translationY = when {
                    newTranslationY > maxDepth -> maxDepth
                    newTranslationY < minHeight -> minHeight
                    else -> newTranslationY
                }
                if (newTranslationY < pullDownThreshold) {
                    view?.translationY = 0f
                    onYChange.invoke(-1f)
                    onCancel.invoke()
                } else {
                    onStartSpin.invoke()
                    view?.translationY = 10f
                    view?.elevation = 0f
                    animateScaleXScaleY(view, 1f, 0.70f)
                }
            }
        }
        return false
    }
    private fun animateScaleXScaleY(view: View?, startValue: Float, endValue: Float) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", startValue, endValue)
        scaleX.duration = 200

        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", startValue, endValue)
        scaleY.duration = 200

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY)
        animatorSet.start()

        animatorSet.doOnEnd {
            (view as? AppCompatImageView)?.setColorFilter(ContextCompat.getColor(view.context, R.color.button_tint))
        }
    }

}

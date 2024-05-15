package com.jar.app.feature_spin.impl.custom.component.outerRing

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.util.SparseArray
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.util.forEach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import com.jar.app.feature_spin.R
import kotlinx.coroutines.*

internal class DoubleCircleLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : RelativeLayout(context, attrs),
    LifecycleOwner {

    private val doubleCircleView: DoubleCircleView = DoubleCircleView(context, null).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }
    // Preallocate and reuse objects
    private val outerRadius: Float
        get() = (measuredWidth.coerceAtMost(measuredHeight) - outerStrokeWidth) * 0.50f
    private val innerRadius: Float
        get() = (measuredWidth.coerceAtMost(measuredHeight) - outerStrokeWidth) * 0.44f
    private val imageSize: Int
        get() = ((outerRadius - innerRadius) * lightBulbSizePercent).toInt()
    private var numberOfBulbs = 24 // number of ImageViews
    private val bulbDrawable = ContextCompat.getDrawable(context, R.drawable.lit_bulb)
    private val litBulbs: SparseArray<LitBulb> = SparseArray<LitBulb>(numberOfBulbs).apply {
        for (i in 0 until numberOfBulbs) {
            put(i, LitBulb(context).apply { setImageDrawable(bulbDrawable) })
        }
    }
    private var isAlreadyDrawn = false
    private val toggleDuration = 300L
    private var isStopped = false
    private var startBulb = 0
    private var endBulb = 5
    private val outerStrokeWidth = 25f
    private val lightBulbSizePercent = 0.6
    private val jobs = mutableListOf<Job>()
    private val litBulbLayoutParams = LayoutParams(0, 0)
    private val point = PointF()

    init {
        addView(doubleCircleView)
        for (i in 0 until numberOfBulbs) {
            addView(litBulbs[i])
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!isInEditMode) {
            val newImageSize = imageSize
            if (litBulbLayoutParams.width != newImageSize || litBulbLayoutParams.height != newImageSize) {
                litBulbLayoutParams.width = newImageSize
                litBulbLayoutParams.height = newImageSize
                for (i in 0 until numberOfBulbs) {
                    litBulbs[i].layoutParams = litBulbLayoutParams
                }
            }
            doubleCircleView.setRadii(innerRadius, outerRadius)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (!isInEditMode) {
            val centerX = width / 2f
            val centerY = height / 2f
            val midRadius = (outerRadius + innerRadius) / 2

            doubleCircleView.layout(0, 0, width, height)

            for (i in 0 until numberOfBulbs) {
                val angle = 2 * Math.PI * i / numberOfBulbs
                val x = centerX + midRadius * kotlin.math.cos(angle).toFloat()
                val y = centerY + midRadius * kotlin.math.sin(angle).toFloat()

                val imageView = litBulbs[i]
                val imageWidth = imageView.measuredWidth
                val imageHeight = imageView.measuredHeight

                point.set(x, y)
                imageView.layout(
                    (point.x - imageWidth / 2).toInt(),
                    (point.y - imageHeight / 2).toInt(),
                    (point.x + imageWidth / 2).toInt(),
                    (point.y + imageHeight / 2).toInt()
                )
            }
        }
    }

    private fun createBlinkAnimation(
        target: LitBulb,
        duration: Long,
        startDelay: Long
    ): ObjectAnimator {
        return ObjectAnimator.ofFloat(target, "alpha", 1f, 0f, 1f).apply {
            setDuration(duration)
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            setStartDelay(startDelay)
        }
    }

    fun toggleImageSource() {
        stopRotationAnimation()
        clearJobs()
        litBulbs.forEach { _, bulb ->
            val job = CoroutineScope(Dispatchers.Main).launch {
                while (true) {
                    bulb.toggleState()
                    delay(toggleDuration)
                }
            }
            jobs.add(job)
        }
    }

    fun clearJobs() {
        jobs.forEach { job ->
            job.cancel()
        }
        jobs.clear()
    }

    fun turnOnAllBulb() {
        isStopped = true
        clearJobs()
        litBulbs.forEach { _, bulb ->
            bulb.turnOnBulb()
        }
    }

    fun turnOffAllBulb() {
        clearJobs()
        litBulbs.forEach { _, bulb ->
            bulb.turnOffBulb()
        }
    }

    fun bulbRotatingAnimation() {
        clearJobs()
        isStopped = false
        startBulb = 0
        endBulb = 5
        lifecycleScope.launch {
            for (i in 0 until numberOfBulbs) {
                litBulbs[i].turnOffBulb()
            }
            for (i in startBulb..endBulb) {
                litBulbs[i].turnOnBulb()
            }
            while (isStopped.not()) {
                litBulbs[startBulb].toggleState()
                endBulb++
                if (endBulb == litBulbs.size()) {
                    endBulb = 0
                }
                litBulbs[endBulb].toggleState()
                delay(150)
                startBulb++
                if (startBulb == litBulbs.size()) {
                    startBulb = 0
                }
            }
        }
    }

    fun stopRotationAnimation() {
        isStopped = true
        for (i in 0 until numberOfBulbs) {
            litBulbs[i].turnOffBulb()
        }
    }

    override val lifecycle: Lifecycle
        get() = LifecycleRegistry(this)
}

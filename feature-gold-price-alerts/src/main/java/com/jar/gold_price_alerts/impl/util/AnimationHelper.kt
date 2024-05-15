package com.jar.gold_price_alerts.impl.util

import com.github.mikephil.charting.data.LineDataSet


import android.animation.ValueAnimator
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import android.animation.Animator
import android.view.animation.AccelerateDecelerateInterpolator

fun animateToZero(chart: LineChart, dataSet: LineDataSet, durationMillis: Long, onCompletion: (() -> Unit)? = null): ValueAnimator? {
    // Store the original Y values
    var animationCancelled = false
    val originalYValues = ArrayList<Float>()
    for (i in 0 until dataSet.entryCount) {
        originalYValues.add(dataSet.getEntryForIndex(i).y)
    }

    // Create a value animator that will iterate from 1 to 0 over the specified duration
    val animator = ValueAnimator.ofFloat(1f, 0f)
    animator.duration = durationMillis
    animator.interpolator = AccelerateDecelerateInterpolator()
    animator.addUpdateListener { animation ->
        val animatedValue = animation.animatedValue as Float

        // Loop over all y-values and scale them by the animated value
        for (i in 0 until dataSet.entryCount) {
            val entry: Entry = dataSet.getEntryForIndex(i)
            val originalY = originalYValues[i]
            entry.y = originalY * animatedValue
        }

        // Notify the chart that the data has changed
        chart.data.notifyDataChanged()
        chart.notifyDataSetChanged()

        // Redraw the chart
        chart.invalidate()
    }

    animator.addListener(object: Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {}

        override fun onAnimationEnd(animation: Animator) {
            if (!animationCancelled)
                onCompletion?.invoke()
                // Check if the animation is not cancelled then
                // Call the completion callback if it was provided
        }

        override fun onAnimationCancel(animation: Animator) {
            animationCancelled = true
        }

        override fun onAnimationRepeat(animation: Animator) {}
    })
    return animator
}
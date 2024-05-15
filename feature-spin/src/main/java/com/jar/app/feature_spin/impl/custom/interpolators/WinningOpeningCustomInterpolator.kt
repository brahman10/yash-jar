package com.jar.app.feature_spin.impl.custom.interpolators

import android.animation.TimeInterpolator

internal class WinningOpeningCustomInterpolator : TimeInterpolator {
    override fun getInterpolation(input: Float): Float {
        // Control points for the Bezier curve
        val x1 = 0.77f
        val y1 = 0.00f
        val x2 = 0.61f
        val y2 = 1.00f

        // Calculate Bezier curve value
        return cubicBezier(input, x1, y1, x2, y2)
    }

    private fun cubicBezier(t: Float, x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val u = 1 - t
        val tt = t * t
        val uu = u * u
        val uuu = uu * u
        val ttt = tt * t

        val p1 = Point(0f, 0f).scale(uuu)
        val p2 = Point(x1, y1).scale(3 * uu * t)
        val p3 = Point(x2, y2).scale(3 * u * tt)
        val p4 = Point(1f, 1f).scale(ttt)

        return p1.add(p2).add(p3).add(p4).y
    }

    data class Point(val x: Float, val y: Float) {
        fun scale(factor: Float) = Point(x * factor, y * factor)
        fun add(other: Point) = Point(x + other.x, y + other.y)
    }
}

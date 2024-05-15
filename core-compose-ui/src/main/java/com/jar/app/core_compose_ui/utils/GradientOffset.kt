package com.jar.app.core_compose_ui.utils

import androidx.compose.ui.geometry.Offset

data class GradientOffset(val start: Offset, val end: Offset)

enum class GradientAngle {
    CW0, CW45, CW90, CW135, CW180, CW225, CW270, CW315
}

fun GradientOffset(angle: GradientAngle = GradientAngle.CW0): GradientOffset {
    return when (angle) {
        GradientAngle.CW45 -> GradientOffset(
            start = Offset.Zero,
            end = Offset.Infinite
        )
        GradientAngle.CW90 -> GradientOffset(
            start = Offset.Zero,
            end = Offset(0f, Float.POSITIVE_INFINITY)
        )
        GradientAngle.CW135 -> GradientOffset(
            start = Offset(Float.POSITIVE_INFINITY, 0f),
            end = Offset(0f, Float.POSITIVE_INFINITY)
        )
        GradientAngle.CW180 -> GradientOffset(
            start = Offset(Float.POSITIVE_INFINITY, 0f),
            end = Offset.Zero,
        )
        GradientAngle.CW225 -> GradientOffset(
            start = Offset.Infinite,
            end = Offset.Zero
        )
        GradientAngle.CW270 -> GradientOffset(
            start = Offset(0f, Float.POSITIVE_INFINITY),
            end = Offset.Zero
        )
        GradientAngle.CW315 -> GradientOffset(
            start = Offset(0f, Float.POSITIVE_INFINITY),
            end = Offset(Float.POSITIVE_INFINITY, 0f)
        )
        else -> GradientOffset(
            start = Offset.Zero,
            end = Offset(Float.POSITIVE_INFINITY, 0f)
        )
    }
}
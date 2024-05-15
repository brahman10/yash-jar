package com.jar.app.feature_lending.impl.ui.common_component

import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun shimmerBrush(
    showShimmer: Boolean = true,
    targetValue: Float = 1000f,
    animation: DurationBasedAnimationSpec<Float> = tween(800),
    repeatMode: RepeatMode = RepeatMode.Restart
): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            Color.Transparent,
            Color.LightGray.copy(alpha = 0.2f),
            Color.Transparent,
        )

        val transition = rememberInfiniteTransition()
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = animation, repeatMode = repeatMode
            )
        )
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation.value, y = translateAnimation.value)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}
package com.jar.app.core_compose_ui.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import java.lang.Math.PI
import kotlin.math.pow

fun Modifier.detectVerticalScroll(
    onScrollUp: () -> Unit = {},
    onScrollDown: () -> Unit = {}
) = composed {
    val currentOnScrollUp by rememberUpdatedState(newValue = onScrollUp)
    val currentOnScrollDown by rememberUpdatedState(newValue = onScrollDown)
    pointerInput(Unit) {
        detectVerticalDragGestures { _, dragAmount ->
            if (dragAmount > 20) currentOnScrollDown()
            if (dragAmount < -20) currentOnScrollUp()
        }
    }
}

fun Modifier.slantShimmer(
    animationSpec: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        animation = tween(
            durationMillis = 2000,
            easing = LinearEasing
        )
    ),
    color: Color = Color.White.copy(0.11f),
    itemWidth: Dp = 34.dp,
    itemHeight: Dp = 34.dp
): Modifier = composed {
    val offsetXAnim = remember { Animatable(-0.5f) }

    LaunchedEffect(offsetXAnim) {
        offsetXAnim.animateTo(
            targetValue = 1f,
            animationSpec = animationSpec
        )
    }

    return@composed this then Modifier
        .graphicsLayer(clip = true)
        .drawWithContent {
            val currentX = offsetXAnim.value * size.width
            drawContent()

            val width = itemWidth.toPx()
            val height = itemHeight.toPx()
            val centerX = width / 2f
            val centerY = height / 2f
            val topPoint = Offset(centerX, centerY - height / 2)
            val rightPoint = Offset(centerX + centerX + width / 4, centerY - height / 2)
            val bottomPoint = Offset(centerX + width / 4, centerY + height / 2)
            val leftPoint = Offset(centerX - width / 2, centerY + height / 2)
            drawPath(
                path = Path().apply {
                    moveTo(topPoint.x + currentX, topPoint.y)
                    lineTo(rightPoint.x + currentX, rightPoint.y)
                    lineTo(bottomPoint.x + currentX, bottomPoint.y)
                    lineTo(leftPoint.x + currentX, leftPoint.y)
                    close()
                },
                color = color
            )
            drawPath(
                path = Path().apply {
                    moveTo(2 * topPoint.x + width / 2 + currentX, topPoint.y)
                    lineTo(2 * rightPoint.x - width + width / 4 + currentX, rightPoint.y)
                    lineTo(2 * bottomPoint.x - width / 4 + currentX, bottomPoint.y)
                    lineTo(2 * leftPoint.x + width + currentX, leftPoint.y)
                    close()
                },
                color = color
            )
        }
}

/**
 * Modifier with a callback that returns a boolean to indicate
 * whether the user has their finger touching down on the screen.
 */
fun Modifier.onTouchHeld(
    onTouch: (Boolean) -> Unit
): Modifier = pointerInput(Unit) {
    awaitEachGesture {
        awaitFirstDown()
        onTouch(true)
        do {
            val event = awaitPointerEvent()
        } while (event.changes.any { it.pressed })
        onTouch(false)
    }
}

fun Modifier.angledGradientBackground(
    colors: List<Color>,
    angle: Float,
    cornerRadius: CornerRadius? = null
) = this.then(
    Modifier.drawBehind {
        val angleRad = angle / 180f * PI
        val x = kotlin.math.cos(angleRad).toFloat() //Fractional x
        val y = kotlin.math.sin(angleRad).toFloat() //Fractional y

        val radius: Float = kotlin.math.sqrt(
            ((size.width.pow(2) + size.height.pow(2))) / 2f
        )
        val offset = center + Offset(x * radius, y * radius)

        val exactOffset = Offset(
            x = kotlin.math.min(offset.x.coerceAtLeast(0f), size.width),
            y = size.height - kotlin.math.min(offset.y.coerceAtLeast(0f), size.height)
        )

        if (cornerRadius == null) {
            drawRect(
                brush = Brush.linearGradient(
                    colors = colors,
                    start = Offset(size.width, size.height) - exactOffset,
                    end = exactOffset
                ),
                size = size
            )
        } else {
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = colors,
                    start = Offset(size.width, size.height) - exactOffset,
                    end = exactOffset
                ),
                size = size,
                cornerRadius = cornerRadius
            )

        }
    }
)

fun Modifier.angledCircularGradientBackground(
    colors: List<Color>,
    angle: Float,
    circleRadius: Float
) = this.then(
    Modifier.drawBehind {
        val angleRad = angle / 180f * PI
        val x = kotlin.math.cos(angleRad).toFloat() //Fractional x
        val y = kotlin.math.sin(angleRad).toFloat() //Fractional y

        val radius: Float = kotlin.math.sqrt(
            ((size.width.pow(2) + size.height.pow(2))) / 2f
        )
        val offset = center + Offset(x * radius, y * radius)

        val exactOffset = Offset(
            x = kotlin.math.min(offset.x.coerceAtLeast(0f), size.width),
            y = size.height - kotlin.math.min(offset.y.coerceAtLeast(0f), size.height)
        )

        drawCircle(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset(size.width, size.height) - exactOffset,
                end = exactOffset
            ), radius = circleRadius
        )
    }
)

fun Modifier.shimmerEffect(
    durationMillis: Int = 2000,
    colors: List<Color> = listOf(Color(0x20D3D3D3), Color(0x20838383), Color(0x20D3D3D3))
): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "shimmerEffect")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(animation = tween(durationMillis)),
        label = "startOffsetX"
    )
    background(
        brush = Brush.linearGradient(
            colors = colors,
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    )
    onGloballyPositioned {
        size = it.size
    }
}
package com.jar.app.feature_lending.impl.ui.realtime_flow.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

data class VerticalDashedLineShape(
    val step: Dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ) = Outline.Generic(Path().apply {
        val stepPx = with(density) { step.toPx() }
        val stepsCount = (size.height / stepPx).roundToInt()
        val actualStep = size.height / stepsCount
        val dotSize = Size(height = actualStep / 2, width = size.width)
        for (i in 0 until stepsCount) {
            addRect(
                Rect(
                    offset = Offset(x = 0f, y = i * actualStep),
                    size = dotSize
                )
            )
        }
        close()
    })
}

@Preview
@Composable
fun DashedVerticalLine(
    modifier: Modifier = Modifier,
    dotsColor: Int = com.jar.app.core_ui.R.color.color_776e94
) {
    Box(
        modifier
            .width(1.dp)
            .height(30.dp)
            .background(colorResource(id = dotsColor), shape = VerticalDashedLineShape(step = 6.dp))
    )
}
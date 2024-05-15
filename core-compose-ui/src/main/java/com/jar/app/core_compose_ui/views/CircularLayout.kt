package com.jar.app.core_compose_ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout

@Composable
fun CircularLayout(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF423C5C),
    content: @Composable () -> Unit
) {
    Layout(
        content = {
            content()
        },
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
    ) { measurable, constraints ->
        val placeable = measurable.first().measure(constraints)
        val size = maxOf(placeable.width, placeable.height)
        layout(size, size) {
            placeable.place((size - placeable.width) / 2, (size - placeable.height) / 2)
        }
    }
}
package com.jar.feature_quests.impl.ui.dashboard_screen

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.ShadowModifier2(
    offsetY: Dp = 15.dp,
    shadowBlurRadius: Dp = 30.dp,
    cornerRadius: Dp = 20.dp,
    transparentColor: Int = Color.Green.toArgb()
): Modifier = composed {
    advancedShadow(
        shadowBlurRadius = shadowBlurRadius,
        color = colorResource(id = com.jar.app.core_ui.R.color.color_4B175B),
        offsetY = offsetY,
        cornersRadius = cornerRadius,
        transparentColor = transparentColor
    )
        .advancedShadow(
            shadowBlurRadius = shadowBlurRadius,
            color = colorResource(id = com.jar.app.core_ui.R.color.color_4B175B),
            offsetY = offsetY,
            cornersRadius = cornerRadius,
            transparentColor = transparentColor
        )
        .advancedShadow(
            shadowBlurRadius = shadowBlurRadius,
            color = colorResource(id = com.jar.app.core_ui.R.color.color_4B175B),
            offsetY = offsetY,
            cornersRadius = cornerRadius,
            transparentColor = transparentColor
        )
        .advancedShadow(
            shadowBlurRadius = shadowBlurRadius,
            color = colorResource(id = com.jar.app.core_ui.R.color.color_4B175B),
            offsetY = offsetY,
            cornersRadius = cornerRadius,
            transparentColor = transparentColor
        )
}

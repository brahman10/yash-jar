package com.jar.app.core_compose_ui.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.jar.app.core_ui.R

@Composable
fun HorizontalProgressBar(screenTimer: Int) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = screenTimer,
                easing = LinearEasing
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(9.dp)
            .background(color = Color.LightGray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.value)
                .height(9.dp)
                .background(color = colorResource(id = R.color.color_7745FF))
        )
    }
}
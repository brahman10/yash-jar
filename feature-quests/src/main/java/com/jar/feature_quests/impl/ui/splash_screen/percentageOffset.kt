package com.jar.feature_quests.impl.ui.splash_screen

import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Stable
fun Modifier.percentageOffset(
    x: Int,
    y: Int
): Modifier = composed {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    this.then(Modifier.offset(screenHeight * x / 100, screenHeight * y / 100))
}
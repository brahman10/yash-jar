package com.jar.app.feature_goal_based_saving.impl.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.jar.app.feature_goal_based_saving.R

@Composable
internal fun defaultTextStyle(): TextStyle {
    val interBold = FontFamily(
        Font(resId = (com.jar.app.core_ui.R.font.inter_bold))
    )
    return TextStyle(
        fontWeight = FontWeight.Normal,
        color = Color.White,
        textAlign = TextAlign.Center

    )
}
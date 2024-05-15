package com.jar.app.core_compose_ui.component

import LogCompositions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.core_compose_ui.theme.robotoFont

/**
 * Jar themed primary button (Gradient Background)
 */
@OptIn(ExperimentalTextApi::class)
@Composable
fun JarCommonText(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = TextStyle.Default,
    fontSize: TextUnit = TextUnit.Unspecified,
    color: Color = Color.Unspecified,
) {
    LogCompositions(tag = "Button", msg = "JarPrimaryButton")
    Text(modifier = modifier, text = text, style = style, fontSize = fontSize, color = color, fontFamily = jarFontFamily)
}

/**
 * Jar themed primary button (Gradient Background)
 */
@Composable
fun JarCommonBoldText(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = TextStyle.Default,
    fontSize: TextUnit = TextUnit.Unspecified,
    color: Color = Color.Unspecified,
) {
    JarCommonText(
        modifier = modifier,
        text = text,
        style = style,
        fontSize = fontSize,
        color = color,
    )
}

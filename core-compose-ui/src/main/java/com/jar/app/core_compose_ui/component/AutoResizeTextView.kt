package com.jar.app.core_compose_ui.component

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.theme.JarColors
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.theme.jarFontFamily

@Composable
fun AutoResizeTextView(
    modifier: Modifier,
    text: String
) {
    val textStyleBody1 = MaterialTheme.typography.body1
    var textStyle by remember { mutableStateOf(textStyleBody1) }
    var readyToDraw by remember { mutableStateOf(false) }
    Text(
        text = text,
        style = textStyle,
        maxLines = 1,
        softWrap = false,
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth) {
                textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.9)
            } else {
                readyToDraw = true
            }
        },
        color = JarColors.color_ACA1d3,
        fontSize = 14.sp,
        fontFamily = jarFontFamily,
    )
}
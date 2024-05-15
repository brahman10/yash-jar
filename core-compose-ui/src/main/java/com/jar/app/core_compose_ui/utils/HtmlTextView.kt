package com.jar.app.core_compose_ui.utils

import android.graphics.Typeface
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat

@Composable
fun HtmlText(text: String, textSize: Float = 28f, typeface: Typeface = Typeface.DEFAULT_BOLD, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                this.textSize = textSize
                this.typeface = typeface
                setText(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY))
            }
        })
}
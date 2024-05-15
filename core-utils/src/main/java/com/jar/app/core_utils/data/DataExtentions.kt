package com.jar.app.core_utils.data

import android.os.Build
import android.text.Html
import android.text.Spanned

fun String.getSpannable(): Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    Html.fromHtml(this, Html.FROM_HTML_OPTION_USE_CSS_COLORS)
} else {
    Html.fromHtml(this)
}
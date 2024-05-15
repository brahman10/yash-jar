package com.jar.app.core_compose_ui.utils

import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.setPadding
import androidx.core.widget.TextViewCompat
import com.jar.app.core_utils.data.getSpannable

fun String.generateAnnotatedFromHtmlString(): AnnotatedString {
    return this.getSpannable().toAnnotatedString()
}

@Composable
fun ComposeLinkText(
    string: String,
    textStyle: Int = com.jar.app.core_ui.R.style.CommonTextViewStyle,
    linkTextColor: Int = com.jar.app.core_ui.R.color.color_EEEAFF,
    textSize: Float = 16f,
    clickHandler: (String) -> Unit
) {
    AndroidView(factory = { context ->
        AppCompatTextView(context).apply {
            text = generateSpannedFromHtmlString(string)
            TextViewCompat.setTextAppearance(this, textStyle)
            setLinkTextColor(ContextCompat.getColor(this.context,linkTextColor ))
            movementMethod = LinkMovementMethod.getInstance()
            val linkClickedListener = object : InternalLinkMovementMethod.OnLinkClickedListener {
                override fun onLinkClicked(url: String?): Boolean {
                    url?.let { clickHandler(it) }
                    return true
                }
            }
            movementMethod = object : InternalLinkMovementMethod(linkClickedListener) {}
            setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
        }
    },
        update = {
            it.text = generateSpannedFromHtmlString(string)
        }
    )

}

/**
 * Compat method that will use the deprecated fromHtml method
 * prior to Android N and the new one after Android N
 */
fun generateSpannedFromHtmlString(html: String?, shouldReplaceNewLine: Boolean = false): Spanned {
    return when (val processedHtml = if (shouldReplaceNewLine) replaceNewlines(html.toString()) else html) {
        null -> {
            // return an empty spannable if the html is null
            SpannableString("")
        }
        else -> {
            HtmlCompat.fromHtml(processedHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }
}

fun replaceNewlines(text: String): String {
    return text.replace("\n", "<br />")
}
package com.jar.health_insurance.impl.ui.components

import android.graphics.Typeface
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat


@Composable
fun HtmlText(
    text: String,
    modifier: Modifier = Modifier,
    customTypeface: Typeface = Typeface.DEFAULT,
    fontSize: Float = 14f,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                textSize = fontSize
                typeface = customTypeface
                setText(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY))
            }
        })
}

@Composable
fun HtmlTextWithClickable(
    text: String,
    modifier: Modifier = Modifier,
    customTypeface: Typeface = Typeface.DEFAULT,
    fontSize: Float = 14f,
    onClick: () -> Unit,
    textColor: Color,
    textAlign: TextAlign,
    spanStart: Int,
    spanEnd: Int
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            TextView(ctx).apply {
                textSize = fontSize
                typeface = customTypeface

                // Create a SpannableString to handle clickable spans
                val spannableString =
                    SpannableString(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY))

                // Find and set clickable spans for links
                val urlSpans =
                    spannableString.getSpans(0, spannableString.length, URLSpan::class.java)
                for (urlSpan in urlSpans) {
                    val start = spannableString.getSpanStart(urlSpan)
                    val end = spannableString.getSpanEnd(urlSpan)
                    val flags = spannableString.getSpanFlags(urlSpan)

                    val clickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            onClick()
                        }
                    }

                    spannableString.setSpan(clickableSpan, start, end, flags)
                }

                // Set the modified SpannableString as the text content
                setText(spannableString)

                // Enable link movement
                movementMethod = LinkMovementMethod.getInstance()
            }
        }
    )
}


@Composable
fun BackPressHandler(
    backPressedDispatcher: OnBackPressedDispatcher? =
        LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher,
    onBackPressed: () -> Unit
) {
    val currentOnBackPressed by rememberUpdatedState(newValue = onBackPressed)

    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                currentOnBackPressed()
            }
        }
    }

    DisposableEffect(key1 = backPressedDispatcher) {
        backPressedDispatcher?.addCallback(backCallback)

        onDispose {
            backCallback.remove()
        }
    }
}
package com.jar.feature_quests.impl.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.LeadingMarginSpan
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.jar.app.feature_quests.R

fun createBulletList(items: Pair<List<String>, List<String>>,context: Context): Spannable {
    val bulletMargin = context.resources.getDimensionPixelSize(R.dimen.bullet_margin)
    val drawable = ContextCompat.getDrawable(context, R.drawable.feature_quest_gradient_bullet)
    val spannable = SpannableStringBuilder()

    items.first.forEachIndexed { index, line ->
        val start = spannable.length
        val formattedLine = HtmlCompat.fromHtml(line, HtmlCompat.FROM_HTML_MODE_LEGACY)
        spannable.append(formattedLine)
        val end = spannable.length

        val bulletSpan = customBulletSpan(drawable, bulletMargin)
        spannable.setSpan(bulletSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

        if (index != items.first.size - 1) {
            spannable.append("\n\n")
        }
    }

    if (items.second.isNotEmpty()) {
        spannable.append("\n\n")
        items.second.forEachIndexed { index, paragraph ->
            val formattedParagraph =
                HtmlCompat.fromHtml(paragraph, HtmlCompat.FROM_HTML_MODE_LEGACY)
            spannable.append(formattedParagraph)

            if (index != items.second.size - 1) {
                spannable.append("\n\n")
            }
        }
    }

    return spannable
}

fun createNumberedList(items: Pair<List<String>, List<String>>,context: Context): Spannable {
    val bulletMargin = context.resources.getDimensionPixelSize(R.dimen.text_margin)
    val spannable = SpannableStringBuilder()

    items.first.forEachIndexed { index, line ->
        val start = spannable.length
        val formattedLine = HtmlCompat.fromHtml(line, HtmlCompat.FROM_HTML_MODE_LEGACY)
        spannable.append(formattedLine)
        val end = spannable.length

        val numberSpan = createNumberedSpan(index + 1,items.first.size, bulletMargin)
        spannable.setSpan(numberSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

        if (index != items.first.size - 1) {
            spannable.append("\n\n")
        }
    }

    if (items.second.isNotEmpty()) {
        spannable.append("\n\n")
        items.second.forEachIndexed { index, paragraph ->
            val formattedParagraph =
                HtmlCompat.fromHtml(paragraph, HtmlCompat.FROM_HTML_MODE_LEGACY)
            spannable.append(formattedParagraph)

            if (index != items.second.size - 1) {
                spannable.append("\n\n")
            }
        }
    }

    return spannable
}

fun customBulletSpan(drawable: Drawable?, margin: Int) : LeadingMarginSpan {
    return object : LeadingMarginSpan {
        override fun getLeadingMargin(first: Boolean): Int {
            return (drawable?.intrinsicWidth ?: 0)*3 + margin
        }

        override fun drawLeadingMargin(
            canvas: Canvas,
            paint: Paint,
            x: Int,
            dir: Int,
            top: Int,
            baseline: Int,
            bottom: Int,
            text: CharSequence?,
            start: Int,
            end: Int,
            first: Boolean,
            layout: Layout
        ) {
            if (first) {
                drawable?.apply {
                    val bulletTop = baseline + (paint.fontMetricsInt.ascent + paint.fontMetricsInt.descent) / 2 - drawable.intrinsicHeight / 2

                    setBounds(
                        x,
                        bulletTop,
                        x + drawable.intrinsicWidth,
                        bulletTop + drawable.intrinsicHeight
                    )
                    draw(canvas)
                }
            }
        }
    }
}

fun createNumberedSpan(number: Int, totalItems: Int, spaceBetweenNumberAndText: Int): LeadingMarginSpan {
    return object : LeadingMarginSpan {
        override fun getLeadingMargin(first: Boolean): Int {
            return (totalItems.toString().length  + 1) * (spaceBetweenNumberAndText / 2)
        }

        override fun drawLeadingMargin(
            canvas: Canvas,
            paint: Paint,
            x: Int,
            dir: Int,
            top: Int,
            baseline: Int,
            bottom: Int,
            text: CharSequence?,
            start: Int,
            end: Int,
            first: Boolean,
            layout: Layout
        ) {
            if (first) {
                canvas.drawText("$number.", x.toFloat(), baseline.toFloat(), paint)

            }
        }
    }
}
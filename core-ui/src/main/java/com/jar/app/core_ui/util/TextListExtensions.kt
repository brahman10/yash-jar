package com.jar.app.core_ui.util

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.UnderlineSpan
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.toSpannable
import com.jar.app.core_base.domain.model.card_library.FontType
import com.jar.app.core_ui.R
import com.jar.app.core_base.domain.model.card_library.TextList
import java.lang.ref.WeakReference

fun TextList.convertToString(context: WeakReference<Context>): Spannable {
    return buildSpannedString {
        if (textList.isEmpty()) return@buildSpannedString
        textList.forEach {
            val text = it.text
            append(text)
            val start = if (this.isEmpty()) 0 else this.length - text.length
            val end = start + text.length

            if (it.fontType.contains(FontType.BOLD.name)) {
                try {
                    this.setSpan(
                        CustomTypefaceSpan(
                            newType = ResourcesCompat.getFont(
                                context.get()!!, R.font.inter_bold
                            )!!
                        ), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } catch (e: Exception) {
                    //android.content.res.Resources$NotFoundException: Font resource ID #0x7f090001 could not be retrieved
                    //Getting above exception in {ResourcesCompat.getFont}
                }
            }

            val size = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                it.textSize.toFloat(),
                context.get()?.resources?.displayMetrics
            )
            this.setSpan(
                AbsoluteSizeSpan(size.toInt(), false), start, end, 0
            )
            it.textColor?.takeIf { it.isNotEmpty() }?.let {
                this.setSpan(
                    ForegroundColorSpan(Color.parseColor(it)),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )
            }
            if (it.fontType.contains(FontType.UNDERLINE.name)) {
                this.setSpan(
                    UnderlineSpan(), start, end, 0
                )
            }

            if(it.fontType.contains(FontType.STRIKETHROUGH.name)){
                this.setSpan(
                    StrikethroughSpan(), start, end, 0
                )
            }
        }
    }.toSpannable()
}
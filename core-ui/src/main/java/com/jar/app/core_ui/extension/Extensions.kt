package com.jar.app.core_ui.extension

import android.graphics.Typeface
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.ViewStub
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.inSpans
import com.jar.app.core_ui.R
import com.jar.app.core_ui.util.CustomTypefaceSpan

inline fun SpannableStringBuilder.font(typeface: Typeface? = null, builderAction: SpannableStringBuilder.() -> Unit) =
    inSpans(StyleSpan(typeface?.style ?: Typeface.DEFAULT.style), builderAction = builderAction)

fun Editable.getStringOrNull(): String? {
    return if (this.isBlank()) null else this.toString()
}

inline fun SpannableStringBuilder.customFont(
    builderAction: SpannableStringBuilder.() -> Unit,
    newTypeFace: Typeface
): SpannableStringBuilder = inSpans(
    CustomTypefaceSpan(
        newType = newTypeFace,
    ), builderAction = builderAction
)

fun ViewStub.isAlreadyInflated(): Boolean {
    if (parent == null) {
        return true
    }
    return false
}

fun Int.digits():List<Int>{
    var value = this
    val digits = ArrayList<Int>()
     while (value!=0){
         digits.add(value%10)
         value /= 10
     }
    digits.reverse()
    return digits.toList()
}

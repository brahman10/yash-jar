package com.jar.app.core_utils.data

import android.text.InputFilter
import android.text.Spannable
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.core.text.isDigitsOnly
import androidx.core.text.toSpannable
import com.jar.app.core_base.util.orFalse

class CardNumberInputFilter(
    private val separatingCharacter: String = " ",
    private val maxCardNumberLength: Int = 16,
    private val textColor: Int = -1,
) : InputFilter {

    companion object {
        private const val MAX_SPACES = 3
    }

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dStart: Int,
        dEnd: Int
    ): CharSequence? {
        if (dest != null && dest.toString().length >= maxCardNumberLength + MAX_SPACES) return ""
        return if (source?.isDigitsOnly().orFalse()) {
            if (source?.length == 1 && (dStart == 4 || dStart == 9 || dStart == 14)) {
                val finalInput = "$separatingCharacter$source".toSpannable()
                finalInput.setSpan(
                    ForegroundColorSpan(textColor),
                    0,
                    finalInput.length,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )
                finalInput
            } else {
                val finalInput = source?.toSpannable()
                finalInput?.setSpan(
                    ForegroundColorSpan(textColor),
                    0,
                    source.length,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )
                finalInput
            }
        } else ""

    }
}
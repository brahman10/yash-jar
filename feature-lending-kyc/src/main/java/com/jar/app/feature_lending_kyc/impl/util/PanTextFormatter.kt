package com.jar.app.feature_lending_kyc.impl.util

import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import androidx.core.text.toSpannable

class PanTextFormatter(
    private val separator: String = " ",
    private val textColor: Int
) : TextWatcher {

    override fun afterTextChanged(s: Editable?) {
        if (s == null) {
            return
        }
        val oldString = s.toString()
        val newString = getNewString(oldString)
        if (newString != oldString) {
            val final = getNewString(oldString).toSpannable()
            final.setSpan(
                ForegroundColorSpan(textColor),
                0,
                final.length,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            s.replace(0, oldString.length, final)
        }
    }

    private fun getNewString(value: String): String {
        val newString = value.replace(separator, "")

        val finalString = if (newString.length in 6..9) {
            newString.substring(0, 5).plus(separator).plus(newString.substring(5, newString.length))
        } else if (newString.length >= 10) {
            newString.substring(0, 5).plus(separator).plus(newString.substring(5, 9).plus(separator).plus(newString[newString.length-1]))
        } else newString

        return finalString
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }
}

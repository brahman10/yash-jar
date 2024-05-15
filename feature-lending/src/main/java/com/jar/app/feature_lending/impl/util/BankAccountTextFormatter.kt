package com.jar.app.feature_lending.impl.util

import android.text.Editable
import android.text.TextWatcher

class BankAccountTextFormatter(
    private val separator: String = " "
) : TextWatcher {

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(p0: Editable?) {
        if (p0 == null ) {
            return
        }
        val oldString = p0.toString()
        val newString = getFormattedAccountNumber(p0.toString())

        if (oldString != newString) {
            p0.replace(0, oldString.length, newString)
        }
    }

    private fun getFormattedAccountNumber(input: String): String {
        return if (input.length <= 10) {
            val formattedText = input.replace(separator, "").chunked(4).joinToString(separator)
            if (formattedText != input) formattedText else input
        } else {
            input
        }
    }

}
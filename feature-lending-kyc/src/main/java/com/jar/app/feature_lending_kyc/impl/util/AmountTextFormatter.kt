package com.jar.app.feature_lending_kyc.impl.util

import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.widget.AppCompatEditText
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.base.util.toIntOrZero

class AmountTextFormatter(
    private val editText: AppCompatEditText
) : TextWatcher {

    override fun afterTextChanged(s: Editable?) {
        if (s == null) {
            return
        }
        var oldString = s.toString()
        val newString = getNewString(oldString)
        editText.removeTextChangedListener(this)
        editText.setText(newString)
        editText.setSelection(newString.length)
        editText.addTextChangedListener(this)

    }

    private fun getNewString(value: String): String {
        val newString = value.replace(",", "")
        return if (newString.isNotEmpty()) {
            newString.toIntOrZero().getFormattedAmount()
        } else {
            ""
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }
}
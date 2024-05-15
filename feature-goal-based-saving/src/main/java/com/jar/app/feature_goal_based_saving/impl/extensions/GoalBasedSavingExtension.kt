package com.jar.app.feature_goal_based_saving.impl.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


internal fun AppCompatEditText.INRCurrencyFormatter() {
    this.addTextChangedListener(object : TextWatcher {
        private var current = ""
        private var previousFormatted = ""
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            if (s.toString() != current) {
                this@INRCurrencyFormatter.removeTextChangedListener(this)

                val cleanString = s.toString().replace("""[$,.]""".toRegex(), "")
                val formatted = cleanString.getCommaFormattedString()
                current = formatted
                this@INRCurrencyFormatter.setText(formatted)
                this@INRCurrencyFormatter.setSelection(formatted.length)
                this@INRCurrencyFormatter.addTextChangedListener(this)
            }
        }
    })
}

internal fun Activity.openKeyboard(view: View) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    inputMethodManager?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

@SuppressLint("MissingPermission")
internal fun vibrate(vibrator: Vibrator) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val duration = 100L // Longer duration in milliseconds (0.5 seconds)
        val amplitude = 100 // Lower amplitude
        val effect = VibrationEffect.createOneShot(duration, amplitude)
        vibrator.vibrate(effect)
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(500) // Longer duration in milliseconds (0.5 seconds)
    }
}

internal fun String.getCommaFormattedString(): String {
    return try {
        val formatted = DecimalFormat("#,##,###", DecimalFormatSymbols.getInstance(Locale("en", "IN"))).format(this.toDouble())
        formatted
    } catch (e: Exception) {
        ""
    }
}


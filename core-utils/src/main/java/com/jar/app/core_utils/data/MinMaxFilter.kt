package com.jar.app.core_utils.data

import android.text.InputFilter
import android.text.Spanned
import com.jar.app.core_base.util.orZero

// Custom class to define min and max for the edit text
class MinMaxFilter(
    private val min: Int = 0,
    private val max: Int = 0
) : InputFilter {

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            val input = Integer.parseInt(dest.toString() + source.toString())
            if (isInRange(min, max, input))
                return null
        } catch (nfe: NumberFormatException) {
        }
        return ""
    }

    private fun isInRange(a: Int, b: Int, c: Int): Boolean {
        return if (b > a) c in a..b else c in b..a
    }
}
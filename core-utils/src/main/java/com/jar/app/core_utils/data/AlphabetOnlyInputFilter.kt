package com.jar.app.core_utils.data

import android.text.InputFilter
import android.text.Spanned
import com.jar.app.core_base.util.orFalse

class AlphabetOnlyInputFilter : InputFilter {

    override fun filter(
        source: CharSequence?,
        p1: Int,
        p2: Int,
        p3: Spanned?,
        p4: Int,
        p5: Int
    ): CharSequence? {
        val containsOnlyAlphabets = source?.all { it.isLetter() || it.isWhitespace() }.orFalse()
        return if (containsOnlyAlphabets) source else ""
    }
}
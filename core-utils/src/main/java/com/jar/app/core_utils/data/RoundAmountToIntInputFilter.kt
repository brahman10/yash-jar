package com.jar.app.core_utils.data

import android.text.InputFilter
import android.text.Spanned
import com.jar.app.base.util.getFormattedAmount
import kotlin.math.ceil

class RoundAmountToIntInputFilter(
    private val shouldRoundToInt: Boolean = true,
    //Input amount may be separated by comma
    private val isInputSeparatedByComma: Boolean = false
) : InputFilter {

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence {
        val text = if (isInputSeparatedByComma) source?.toString()?.replace(",", "") else source
        text?.toString()?.toFloatOrNull()?.let {
            return if (shouldRoundToInt)
                if (isInputSeparatedByComma) ceil(it).getFormattedAmount()
                else ceil(it).toString()
            else text.toString()
        } ?: kotlin.run {
            return ""
        }
    }

}
package com.jar.app.feature_lending.impl.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.jar.app.feature_lending.impl.ui.realtime_flow_with_camps.pan.enter_pan.EnterPanNumberFragment.Companion.PAN_LENGTH

class PanVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val newString = text.toString().replace(" ", "")

        val formattedString = formatPan(newString)

        return TransformedText(
            AnnotatedString(formattedString),
            panNumberOffsetTranslator
        )
    }

    private fun formatPan(panNumber: String): String {
        val formattedPan = StringBuilder()

        for (i in panNumber.indices) {
            if (i == 5 || i == 9) {
                formattedPan.append(' ')
            }
            formattedPan.append(panNumber[i])
        }

        return formattedPan.toString()
    }

    private val panNumberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            return when {
                offset <= 5 -> offset
                offset <= 9 -> offset + 1
                else -> offset + 2
            }
        }

        override fun transformedToOriginal(offset: Int): Int {
            return when {
                offset <= 5 -> offset
                offset <= 10 -> offset - 1
                else -> PAN_LENGTH
            }
        }
    }
}

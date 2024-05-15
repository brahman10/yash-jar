package com.jar.app.core_compose_ui.utils

import android.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.jar.app.base.util.orFalse
import com.jar.app.core_base.domain.model.card_library.FontType
import com.jar.app.core_base.domain.model.card_library.TextData
import com.jar.app.core_compose_ui.theme.jarFontFamily

fun convertToAnnotatedString(textList: List<TextData>, separator: String = "\n"): AnnotatedString {
    return buildAnnotatedString {
        if (textList.isEmpty()) return@buildAnnotatedString
        textList.forEach {
            val text = it.text

            var textStyle = SpanStyle(fontFamily = jarFontFamily)

            if (it.fontType?.contains(FontType.BOLD.name).orFalse()) {
                textStyle = textStyle.copy(fontWeight = FontWeight.Bold)
            }
            if (it.fontType?.contains(FontType.UNDERLINE.name).orFalse()) {
                textStyle = textStyle.copy(textDecoration = TextDecoration.Underline)
            }
            if (it.fontType?.contains(FontType.STRIKETHROUGH.name).orFalse()) {
                textStyle = textStyle.copy(textDecoration = TextDecoration.LineThrough)
            }
            textStyle = textStyle.copy(
                fontSize = it.textSize.sp,
                color = androidx.compose.ui.graphics.Color(Color.parseColor(it.textColor))
            )
            withStyle(textStyle) {
                append(text + separator)
            }
        }
    }
}
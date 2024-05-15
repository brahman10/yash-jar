package com.jar.app.core_compose_ui.views

import androidx.compose.runtime.Composable
import androidx.annotation.ColorRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.theme.frauncesFontFamily
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.core_compose_ui.utils.GradientAngle
import com.jar.app.core_compose_ui.utils.GradientOffset
import kotlin.math.ceil

// https://stackoverflow.com/a/69947555
@Composable
fun WrapTextContent(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    SubcomposeLayout(modifier) { constraints ->
        val composable = @Composable { localOnTextLayout: (TextLayoutResult) -> Unit ->
            Text(
                text = text,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                letterSpacing = letterSpacing,
                textDecoration = textDecoration,
                textAlign = textAlign,
                lineHeight = lineHeight,
                overflow = overflow,
                softWrap = softWrap,
                maxLines = maxLines,
                onTextLayout = localOnTextLayout,
                style = style,
            )
        }
        var textWidthOpt: Int? = null
        subcompose("measureView") {
            composable { layoutResult ->
                textWidthOpt = (0 until layoutResult.lineCount)
                    .maxOf { line ->
                        ceil(layoutResult.getLineRight(line) - layoutResult.getLineLeft(line)).toInt()
                    }
            }
        }[0].measure(constraints)
        val textWidth = textWidthOpt ?: constraints.minWidth
        val placeable = subcompose("content") {
            composable(onTextLayout)
        }[0].measure(constraints.copy(minWidth = textWidth, maxWidth = textWidth))

        layout(width = textWidth, height = placeable.height) {
            placeable.place(0, 0)
        }
    }
}
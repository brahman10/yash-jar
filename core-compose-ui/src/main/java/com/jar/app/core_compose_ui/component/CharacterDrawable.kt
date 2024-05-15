package com.jar.app.core_compose_ui.component

import androidx.annotation.FloatRange
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.android.showkase.annotation.ShowkaseComposable
import kotlin.random.Random

@OptIn(ExperimentalTextApi::class)
@Composable
fun CharacterDrawable(
    modifier: Modifier,
    character: String,
    drawableSize: DpSize = 56.dp.toDpSize(),
    cornerRadius: Dp = 64.dp,
    borderStroke: BorderStroke? = null
) {
    val text = remember { character.take(2) }
//    val borderWidth = remember { borderStroke?.width?.value ?: 0f }
    val textMeasurer = rememberTextMeasurer()
    val bgColor = remember { getColorByCharacter(text) }
    val rectCornerRadius = remember { CornerRadius(cornerRadius.value, cornerRadius.value) }
    val textLayoutResult = remember {
        textMeasurer.measure(
            text = AnnotatedString(text),
            style = TextStyle(
                fontSize = drawableSize.width.value.div(2f).sp,
                color = if (bgColor.isColorDark()) Color.White else Color.Black
            )
        )
    }
    val textSize = textLayoutResult.size
    Canvas(
        modifier = modifier
            .size(drawableSize)
            .padding(2.dp)
    ) {

        drawRoundRect(
            color = bgColor,
            cornerRadius = rectCornerRadius,
            style = Fill
        )
        borderStroke?.let {
            drawRoundRect(
                brush = borderStroke.brush,
                cornerRadius = rectCornerRadius,
                style = Stroke(
                    width = it.width.value
                )
            )
        }
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(
                (size.width - textSize.width) / 2f,
                (size.height - textSize.height) / 2f
            ),
        )

    }
}

private fun getColorByCharacter(character: String): Color {
    val rnd = Random(character.hashCode())
    return Color(red = rnd.nextInt(256), green = rnd.nextInt(256), blue = rnd.nextInt(256))
}

private fun Color.isColorDark(@FloatRange(from = 0.0, to = 1.0) threshold: Float = 0.9f): Boolean {
    val darkness =
        1 - (this.red * 0.299 + this.green * 0.587 + this.blue * 0.114) / 255
    return darkness >= threshold
}

@Preview
@ShowkaseComposable(skip = true)
@Composable
fun CharacterDrawablePreview() {
    CharacterDrawable(
        modifier = Modifier,
        character = "M",
        drawableSize = 100.dp.toDpSize(),
        cornerRadius = 80.dp,
        borderStroke = BorderStroke(6.dp, Color.White)
    )
}
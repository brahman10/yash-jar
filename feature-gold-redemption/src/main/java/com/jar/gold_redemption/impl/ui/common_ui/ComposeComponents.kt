package com.jar.gold_redemption.impl.ui.common_ui

import android.util.Log
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.jar.app.core_compose_ui.views.WrapTextContent
import com.jar.app.core_compose_ui.R

@Composable
fun getGoldGradientColorList(): List<Color> {
    return listOf(
        colorResource(id = com.jar.app.core_ui.R.color.color_93722f),
        colorResource(id = com.jar.app.core_ui.R.color.color_9c8350),
        colorResource(id = com.jar.app.core_ui.R.color.color_bfa673),
        colorResource(id = com.jar.app.core_ui.R.color.color_dbc28f),
        colorResource(id = com.jar.app.core_ui.R.color.color_886f3c),
        colorResource(id = com.jar.app.core_ui.R.color.color_765e2a)
    )
}

@Composable
fun getSilverGradientColorList(): List<Color> {
    return listOf(
        colorResource(id = com.jar.app.core_ui.R.color.color_93722f),
        colorResource(id = com.jar.app.core_ui.R.color.color_9c8350),
        colorResource(id = com.jar.app.core_ui.R.color.color_bfa673),
        colorResource(id = com.jar.app.core_ui.R.color.color_dbc28f),
        colorResource(id = com.jar.app.core_ui.R.color.color_886f3c),
        colorResource(id = com.jar.app.core_ui.R.color.color_765e2a)
    )
}

@Composable
@Preview
fun PreviewGoldText() {
    GoldText(
        text = "Celebrate every\noccasion with gold\nand diamond\nvouchers.",
        textAlign = TextAlign.Center,
    )
}

@Composable
@Preview
fun PreviewGoldButton() {
    GoldButton(
        text = "Jewellery Vouchers",
        fontSize = JarTypography.body1.fontSize,
        fontFamily = frauncesFontFamily
    )
}

@Composable
@Preview
fun PreviewGoldBorder() {
    Box(
        Modifier
            .size(100.dp)
            .background(colorResource(id = com.jar.app.core_ui.R.color.color_121127))
            .goldBorder(),
        contentAlignment = Alignment.Center
    ) {
        Text("HEllo", color = Color.Red)
    }
}

val GoldRound = RoundedCornerShape(20.dp)

fun Modifier.goldBorder(
    shape: Shape = GoldRound,
    width: Dp = 1.dp
) = composed {
    border(
        width = width, brush = Brush.linearGradient(
            colors = getGoldGradientColorList(),
            tileMode = TileMode.Mirror
        ), shape = shape
    )
}

@Composable
fun GoldButton(
    modifier: Modifier = Modifier,
    text: String,
    @ColorRes backgroundColor: Int = com.jar.app.core_ui.R.color.color_982D32,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontFamily: FontFamily = frauncesFontFamily
) {
    Box(
        modifier = modifier
            .background(
                colorResource(id = backgroundColor),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        VerticalGradientGoldText2(text, fontSize, fontFamily)
    }
}

@Composable
@Preview
fun VerticalGradientGoldText2Preview() {
    VerticalGradientGoldText2("How do vouchers work?", fontSize = 16.sp, fontFamily = frauncesFontFamily)
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun VerticalGradientGoldText2(
    text: String,
    fontSize: TextUnit,
    fontFamily: FontFamily = jarFontFamily
) {
    val offset = GradientOffset(GradientAngle.CW90)
    val colorsStops = arrayOf(
        0.00f to colorResource(R.color.color_FFFFFFFF),
        0.7f to colorResource(R.color.color_1CCBC4B3)
    )
    Text(
        modifier = Modifier
            .graphicsLayer(alpha = 0.99f)
            .drawWithCache {
                val brush =
                    Brush.radialGradient(
                        colorStops = colorsStops,
                        center = Offset(0f, 0f),
                        radius = this.size.center.x * 1.4f
                    )
                onDrawWithContent {
                    drawContent()
                    drawRect(brush, blendMode = BlendMode.SrcAtop, style = Stroke(width = 1f))
//                    drawRect(brush, blendMode = BlendMode.SrcAtop)
                }
            }
        ,
        text = text,
        style = TextStyle(
            brush = Brush.linearGradient(
                colorStops = arrayOf(
                    0f to colorResource(R.color.color_FF93722F),
                    0.1f to colorResource(R.color.color_FF9C8350),
                    0.5f to colorResource(R.color.color_FFDBC28F),
                    0.65f to colorResource(R.color.color_FFBFA673),
                    0.9f to colorResource(R.color.color_FF886F3C),
                    0.98f to colorResource(R.color.color_FF765E2A)
                ),
                tileMode = TileMode.Mirror,
                start = offset.start,
                end = offset.end
            ),
            fontFamily = fontFamily,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Normal
        ),
        fontSize = fontSize,
    )
}

@Composable()
@Preview
fun GreenBannerGoldPreview() {
    GreenBannerGold(text = "Gold worth #213 will be added to your Jar locker will be", bgColor = Color.Transparent, modifier = Modifier.padding(bottom = 12.dp), fontSize = 16.sp)
}
@Composable()
fun GreenBannerGold(
    text: String? = null, shape: Shape = RectangleShape, modifier: Modifier = Modifier,
    bgColor: Color = Color(0.12f, 0.65f, 0.53f, 0.15f),
    fontSize: TextUnit = 14.sp
) {
    Row(
        modifier
            .background(bgColor, shape = shape)
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = com.jar.app.feature_gold_redemption.R.drawable.feature_gold_redemption_buy_gold),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.width(6.dp))
        // https://issuetracker.google.com/issues/206039942
        text.takeIf { !it.isNullOrBlank() }?.let {
            WrapTextContent(
                text ?: " ",
                Modifier.padding(vertical = 16.0.dp),
                color = Color(0.35f, 0.87f, 0.78f, 1.0f),
                style = JarTypography.body1,
                fontSize = fontSize,
                maxLines = 2,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun GoldText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign = TextAlign.Left,
    fontStyle: FontStyle = FontStyle.Normal,
    fontWeight: FontWeight = FontWeight.Normal,
    fontFamily: FontFamily = frauncesFontFamily
) {
    val goldColors = arrayOf(
        0.00f to colorResource(R.color.color_FFFFFFFF),
        0.7f to colorResource(R.color.color_1CCBC4B3)
    )
    Text(
        modifier = modifier
            .graphicsLayer(alpha = 0.99f)
            .drawWithCache {
                val brush =
                    Brush.radialGradient(
                        colorStops = goldColors,
                        center = Offset(0f, 0f),
                        radius = this.size.center.x * 1.4f
                    )
                onDrawWithContent {
                    drawContent()
                    drawRect(brush, blendMode = BlendMode.SrcAtop)
                }
            },
        text = text,
        style = TextStyle(
            brush = Brush.linearGradient(
                colorStops = arrayOf(
                    0.04f to colorResource(R.color.color_FF93722F),
                    0.2f to colorResource(R.color.color_FF9C8350),
                    0.5f to colorResource(R.color.color_FFDBC28F),
                    0.65f to colorResource(R.color.color_FFBFA673),
                    0.82f to colorResource(R.color.color_FF886F3C),
                    0.98f to colorResource(R.color.color_FF765E2A)
                ),
                tileMode = TileMode.Mirror
            ),
            fontFamily = fontFamily,
            fontStyle = fontStyle,
            fontWeight = fontWeight
        ),
        fontSize = fontSize,
        textAlign = textAlign,
    )
}
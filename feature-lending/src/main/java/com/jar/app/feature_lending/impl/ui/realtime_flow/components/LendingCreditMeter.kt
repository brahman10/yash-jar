package com.jar.app.feature_lending.impl.ui.realtime_flow.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.theme.jarFontFamily
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@OptIn(ExperimentalTextApi::class)
@Composable
fun CreditMeter(
    modifier: Modifier = Modifier,
    creditScore: Int,
    startAngle: Float, //160
    endAngle: Float,  //365
    progressAngle: Float,
    arcWidth: Dp = 20.dp,
    indicatorWidth: Dp = 8.dp,
    colorList: List<Color> = arrayListOf(
        Color(0xFFC42742),
        Color(0xFFFF8933),
        Color(0xFFF4C430),
        Color(0xFF1EC664)
    ),
    centerInnerCircleColor: Color = Color(0xFFEEEAFF),
    centerOuterCircleColor: Color = Color(0xFFAB8CFF),
    indicatorColor: Color = Color(0xFFEEEAFF),
    indicatorShadowColor: Color = Color(0x40000000),
    maxValue: Float = MAX_CREDIT_SCORE_VALUE,
    minValue: Float = MIN_CREDIT_SCORE_VALUE,
    title: String = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_your_score.resourceId)
) {
    val textMeasure = rememberTextMeasurer()

    val titleTextLayout = remember(title, textTitleStyle) {
        textMeasure.measure(title, textTitleStyle)
    }

    val scoreTextLayout = remember(creditScore.toString(), textScoreStyle) {
        textMeasure.measure(creditScore.toString(), textScoreStyle)
    }
    var score by rememberSaveable(key = creditScore.toString()) { mutableStateOf(0) }
    val scoreCounter by animateIntAsState(
        targetValue = score,
        animationSpec = tween(
            durationMillis = 2000,
            easing = FastOutSlowInEasing
        ),
        label = "scoreCounterAnimation"
    )
    LaunchedEffect(key1 = creditScore) {
        score = creditScore
    }
    val average = maxValue - minValue // 600

    val arcGapSpace = 5f
    val redEndValue = 539
    val orangeEndValue = 719
    val yellowEndValue = 809

    val redEndAngle =
        startAngle + (endAngle - startAngle) * ((redEndValue - minValue) / average) //
    val orangeEndAngle =
        startAngle + (endAngle - startAngle) * ((orangeEndValue - minValue) / average)//
    val yellowEndAngle =
        startAngle + (endAngle - startAngle) * ((yellowEndValue - minValue) / average) //
    val greenEndAngle = startAngle + (endAngle - startAngle) //
    Canvas(
        modifier = modifier
            .size(width = 166.dp, height = 116.dp)
            .clipToBounds()
    ) {
        val arcRadius = size.minDimension / 1.8f
        val arcCenter = Offset(size.width / 2, size.height / 1.17f)


        // Drawing red arc
        drawArc(
            color = colorList[0],
            startAngle = startAngle,
            sweepAngle = redEndAngle - startAngle,
            useCenter = false,
            topLeft = arcCenter - Offset(arcRadius, arcRadius),
            size = Size(arcRadius * 2, arcRadius * 2),
            style = Stroke(width = arcWidth.toPx())
        )

        // Drawing transparent gap arc
        drawArc(
            color = Color.Transparent,
            startAngle = redEndAngle,
            sweepAngle = arcGapSpace,
            useCenter = false,
            topLeft = arcCenter - Offset(arcRadius, arcRadius),
            size = Size(arcRadius * 2, arcRadius * 2),
            style = Stroke(width = arcWidth.toPx())
        )

        // Drawing orange arc
        drawArc(
            color = colorList[1],
            startAngle = redEndAngle + arcGapSpace,
            sweepAngle = orangeEndAngle - redEndAngle,
            useCenter = false,
            topLeft = arcCenter - Offset(arcRadius, arcRadius),
            size = Size(arcRadius * 2, arcRadius * 2),
            style = Stroke(width = arcWidth.toPx())
        )


        // Drawing transparent gap arc
        drawArc(
            color = Color.Transparent,
            startAngle = orangeEndAngle + arcGapSpace,
            sweepAngle = arcGapSpace,
            useCenter = false,
            topLeft = arcCenter - Offset(arcRadius, arcRadius),
            size = Size(arcRadius * 2, arcRadius * 2),
            style = Stroke(width = 30f)
        )

        // Drawing yellow arc
        drawArc(
            color = colorList[2],
            startAngle = orangeEndAngle + 2 * arcGapSpace,
            sweepAngle = yellowEndAngle - orangeEndAngle,
            useCenter = false,
            topLeft = arcCenter - Offset(arcRadius, arcRadius),
            size = Size(arcRadius * 2, arcRadius * 2),
            style = Stroke(width = arcWidth.toPx())
        )

        // Drawing transparent gap arc
        drawArc(
            color = Color.Transparent,
            startAngle = yellowEndAngle + 2 * arcGapSpace,
            sweepAngle = arcGapSpace,
            useCenter = false,
            topLeft = arcCenter - Offset(arcRadius, arcRadius),
            size = Size(arcRadius * 2, arcRadius * 2),
            style = Stroke(width = 30f)
        )

        // Drawing green arc
        drawArc(
            color = colorList[3],
            startAngle = yellowEndAngle + 3 * arcGapSpace,
            sweepAngle = greenEndAngle - yellowEndAngle,
            useCenter = false,
            topLeft = arcCenter - Offset(arcRadius, arcRadius),
            size = Size(arcRadius * 2, arcRadius * 2),
            style = Stroke(width = arcWidth.toPx())
        )

        val w = size.width
        val h = size.height

        val shadowGap = 4
        drawLine(
            start = Offset(x = (arcCenter.x) - shadowGap, y = arcCenter.y + shadowGap),
            end = Offset(
                x = (arcCenter.x + arcRadius * cos(Math.toRadians(progressAngle.toDouble())).toFloat()) - shadowGap,
                y = (arcCenter.y + arcRadius * sin(Math.toRadians(progressAngle.toDouble())).toFloat()) + shadowGap
            ),
            color = indicatorShadowColor,
            strokeWidth = indicatorWidth.toPx(),
            cap = StrokeCap.Round,
        )

        drawLine(
            start = Offset(x = arcCenter.x, y = arcCenter.y),
            end = Offset(
                x = arcCenter.x + arcRadius * cos(Math.toRadians(progressAngle.toDouble())).toFloat(),
                y = arcCenter.y + arcRadius * sin(Math.toRadians(progressAngle.toDouble())).toFloat()
            ),
            color = indicatorColor,
            strokeWidth = indicatorWidth.toPx(),
            cap = StrokeCap.Round,
        )

        drawCircle(
            color = centerOuterCircleColor,
            radius = w / 5,
            center = arcCenter
        )
        drawCircle(
            color = centerInnerCircleColor,
            radius = w / 6,
            center = arcCenter
        )

        drawText(
            textMeasurer = textMeasure,
            text = title,
            style = textTitleStyle,
            topLeft = Offset(
                x = arcCenter.x - titleTextLayout.size.width / 2,
                y = arcCenter.y - scoreTextLayout.size.height / 1.6f,
            )
        )

        drawText(
            textMeasurer = textMeasure,
            text = scoreCounter.toString(),
            style = textScoreStyle,
            topLeft = Offset(
                x = arcCenter.x - scoreTextLayout.size.width / 2,
                y = (arcCenter.y - scoreTextLayout.size.height / 2) + 8.dp.value,
            ),
            maxLines = 1
        )
    }
}

private val textTitleStyle = TextStyle(
    fontSize = 8.sp,
    fontFamily = jarFontFamily,
    fontWeight = FontWeight(400),
    color = Color.Black,
)

private val textScoreStyle = TextStyle(
    fontSize = 18.sp,
    fontFamily = jarFontFamily,
    fontWeight = FontWeight(800),
    color = Color.Black,
)

@Preview()
@Composable
fun CreditMeterPreview() {

    val startAngle = START_ANGLE_CREDIT_METER_VALUE
    val endAngle = END_ANGLE_CREDIT_METER_VALUE
    val max = MAX_CREDIT_SCORE_VALUE
    val min = MIN_CREDIT_SCORE_VALUE
    val creditScore =860// minimum progress 300 and progress is between 300..900
    val angle =
        (startAngle + (endAngle - startAngle) * ((creditScore.toFloat() - min) / (max - min)))
    var offset = 0
    for(i in scoreRanges.indices){
        if(scoreRanges[i].contains(creditScore)){
            offset += i * 5
            break
        }
    }
    val targetAngle =  if(creditScore<350) max(angle + offset,START_ANGLE_CREDIT_METER_VALUE+10)
    else if (creditScore>850) min(angle + offset,END_ANGLE_CREDIT_METER_VALUE+6)
    else angle + offset
    Column(
        modifier = Modifier
    ) {
        CreditMeter(
            creditScore = creditScore,
            startAngle = startAngle,
            endAngle = endAngle,
            progressAngle = targetAngle
        )
    }
}
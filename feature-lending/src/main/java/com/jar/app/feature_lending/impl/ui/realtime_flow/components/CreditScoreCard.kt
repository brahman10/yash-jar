package com.jar.app.feature_lending.impl.ui.realtime_flow.components

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.CreditScoreCard
import kotlin.math.max
import kotlin.math.min


const val MAX_CREDIT_SCORE_VALUE = 900f
const val MIN_CREDIT_SCORE_VALUE = 300f
const val START_ANGLE_CREDIT_METER_VALUE = 160f
const val END_ANGLE_CREDIT_METER_VALUE = 365f
val scoreRanges =
    listOf<IntRange>(IntRange(300, 539), IntRange(540, 719), IntRange(720, 809), IntRange(810, 900))

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CreditScoreCard(
    modifier: Modifier = Modifier,
    creditScoreCard: CreditScoreCard,
    backgroundColor: Color = Color(creditScoreCard.backgroundColor.toColorInt()),
    creditMeterAnimationDuration: Int = 2000,
    footerTextAnimationDuration: Int = 1000,
    shouldShowDivider: Boolean = true
) {
    val startAngle = START_ANGLE_CREDIT_METER_VALUE
    val endAngle = END_ANGLE_CREDIT_METER_VALUE
    val max = MAX_CREDIT_SCORE_VALUE
    val min = MIN_CREDIT_SCORE_VALUE
    var targetAngle by rememberSaveable(key = "key_${creditScoreCard.creditScore}") {
        mutableStateOf(startAngle)
    }
    val progressAngle by animateFloatAsState(
        targetValue = targetAngle,
        animationSpec = tween(durationMillis = creditMeterAnimationDuration), label = ""
    )
    LaunchedEffect(key1 = creditScoreCard.creditScore) {
        val angle =
            (startAngle + (endAngle - startAngle) * ((creditScoreCard.creditScore.toFloat() - min) / (max - min)))
        var offset = 0
        for (i in scoreRanges.indices) {
            if (scoreRanges[i].contains(creditScoreCard.creditScore)) {
                offset += i * 5
                break
            }
        }
        targetAngle = if (creditScoreCard.creditScore < 350) max(
            angle + offset,
            START_ANGLE_CREDIT_METER_VALUE + 10
        )
        else if (creditScoreCard.creditScore > 850) min(
            angle + offset,
            END_ANGLE_CREDIT_METER_VALUE + 6
        )
        else angle + offset
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(CornerSize(8.dp)))
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CreditMeter(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            creditScore = creditScoreCard.creditScore,
            startAngle = startAngle,
            endAngle = endAngle,
            maxValue = max,
            minValue = min,
            progressAngle = progressAngle
        )

        var footerTextVisibleState by rememberSaveable { mutableStateOf(false) }
        AnimatedContent(
            targetState = progressAngle, label = "",
        ) { progress ->
            footerTextVisibleState = progress == targetAngle
            AnimatedVisibility(
                visible = footerTextVisibleState,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = footerTextAnimationDuration,
                        easing = LinearEasing
                    )
                ) + expandVertically(
                    expandFrom = Alignment.Top,
                    animationSpec = tween(
                        durationMillis = footerTextAnimationDuration,
                        easing = LinearEasing
                    ),
                ),
                exit = fadeOut(animationSpec = tween(1)) + shrinkVertically(
                    shrinkTowards = Alignment.Top,
                    animationSpec = tween(1)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Text(
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.CenterHorizontally),
                        text = creditScoreCard.creditScoreResult,
                        style = JarTypography.body2.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(id = com.jar.app.core_ui.R.color.commonTxtColor)
                        ),
                    )
                    if (shouldShowDivider) {
                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            painter = painterResource(id = R.drawable.feature_lending_bg_divider_trust_brand),
                            contentDescription = null
                        )

                        Text(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .align(Alignment.CenterHorizontally),
                            text = creditScoreCard.footerText,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            style = JarTypography.body2,
                            color = colorResource(id = com.jar.app.core_ui.R.color.commonTxtColor),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

        }
    }
}

@Preview
@Composable
fun PreviewCreditScoreCard() {
    CreditScoreCard(
        creditScoreCard = CreditScoreCard(
            backgroundColor = "#FF492B9D",
            footerText = "Booyah!",
            creditScore = 900,
            "Good"
        )
    )
}
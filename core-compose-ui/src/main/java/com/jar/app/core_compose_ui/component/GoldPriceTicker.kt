@file:OptIn(ExperimentalFoundationApi::class)

package com.jar.app.core_compose_ui.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jar.app.base.util.milliSecondsToCountDown
import com.jar.app.core_compose_ui.R
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.utils.slantShimmer
import com.jar.app.core_ui.R.*
import kotlin.time.Duration.Companion.seconds


@Composable
fun GoldPriceTicker(
    modifier: Modifier = Modifier,
    tint: Color = colorResource(id = color.color_3C3357),
    goldPrice: Float,
    validityInSeconds: Long,
    currentSecond: Long,
    isLiveChipVisible: Boolean = true
) {
    Crossfade(
        modifier = modifier.background(tint),
        targetState = validityInSeconds,
        label = ""
    ) { validity ->
        when (currentSecond) {
            validity -> NewPriceFetched()
            in 0..10 -> PriceAboutToChange(timeLeftInSeconds = currentSecond)
            else -> TimerActive(
                validityInSeconds = validity,
                currentSecond = currentSecond,
                livePriceText = stringResource(id = R.string.gold_sell_price, goldPrice),
                isLiveChipVisible = isLiveChipVisible
            )
        }
    }
}

@Preview
@Composable
fun GoldPriceTickerPreview() {
    GoldPriceTicker(
        goldPrice = 5240f,
        validityInSeconds = 160,
        currentSecond = 5
    )
}

@Composable
private fun NewPriceFetched() {
    Row(
        modifier = Modifier
            .background(color = colorResource(id = color.color_3C3357))
            .slantShimmer()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.new_gold_price_fetched),
            style = JarTypography.caption,
            color = colorResource(id = color.color_58DDC8)
        )
    }
}

@Composable
private fun TimerActive(
    validityInSeconds: Long,
    currentSecond: Long,
    livePriceText: String,
    isLiveChipVisible: Boolean
) {
    val highlightColor = colorResource(id = color.color_789BDE).copy(alpha = 0.32f)
    val highlightWidthAnim = remember(validityInSeconds) {
        Animatable(
            initialValue = if (validityInSeconds > 0) currentSecond / validityInSeconds.toFloat() else 1f
        )
    }

    LaunchedEffect(Unit) {
        highlightWidthAnim.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = validityInSeconds.seconds.inWholeMilliseconds.toInt(),
                easing = LinearEasing
            )
        )
    }
    Row(
        modifier = Modifier
            .drawBehind {
                clipRect(right = this.size.width * highlightWidthAnim.value) {
                    drawRect(
                        color = highlightColor,
                        size = size
                    )
                }
            }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .bringIntoViewRequester(
                remember { BringIntoViewRequester() }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLiveChipVisible) {
            LiveChip()
            Spacer(modifier = Modifier.size(6.dp))
        }
        Text(
            modifier = Modifier.weight(1f),
            text = livePriceText,
            style = JarTypography.caption,
            color = Color.White
        )
        Text(
            text = stringResource(
                R.string.gold_price_valid_for,
                currentSecond.seconds.inWholeMilliseconds.milliSecondsToCountDown()
            ),
            style = JarTypography.caption,
            color = Color.White
        )
    }
}

@Composable
private fun PriceAboutToChange(timeLeftInSeconds: Long) {
    Row(
        modifier = Modifier
            .background(color = colorResource(id = color.color_3C3357))
            .slantShimmer()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .bringIntoViewRequester(
                remember { BringIntoViewRequester() }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(
                R.string.gold_price_is_about_to_change,
                timeLeftInSeconds.seconds.inWholeMilliseconds.milliSecondsToCountDown()
            ),
            style = JarTypography.caption,
            color = colorResource(id = color.color_58DDC8)
        )
    }
}


@Composable
private fun LiveChip() {
    Row(
        modifier = Modifier
            .background(
                color = colorResource(id = color.color_3C3357),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 7.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = Modifier
                .size(6.dp)
                .background(color = colorResource(id = color.color_EB6A6E), shape = CircleShape)
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = "LIVE",
            style = JarTypography.overline,
            color = colorResource(id = color.color_EB6A6E)
        )
    }
}



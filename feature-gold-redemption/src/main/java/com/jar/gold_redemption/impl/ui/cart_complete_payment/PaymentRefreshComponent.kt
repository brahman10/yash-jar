package com.jar.gold_redemption.impl.ui.cart_complete_payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jar.app.base.util.secondsToMinsAndSecs
import com.jar.app.core_compose_ui.component.debounceClickable
import kotlinx.coroutines.delay


@Composable
fun RenderRefreshButton(refreshButtonPressed: (() -> Unit)?) {
    val initTick: Long = 300

    var ticks by remember(initTick) {
        mutableStateOf(initTick)
    }
    var isEnabled by remember {
        mutableStateOf(false)
    }

    RenderButton(ticks, isEnabled, refreshButtonPressed)

    LaunchedEffect(ticks) {
        val diff = ticks - 1
        delay(1000)
        ticks = diff
        if (ticks <= 0) {
            isEnabled = true
        }
    }
}

@Composable
fun RenderButton(ticks: Long, isEnabled: Boolean, refreshButtonPressed: (() -> Unit)?) {
    val backgroundColor =
        colorResource(id = if (isEnabled) com.jar.app.core_ui.R.color.color_7745FF else com.jar.app.feature_gold_redemption.R.color.color_7745FF_30)
    val textColor = if (isEnabled) Color.White else Color.White.copy(alpha = 0.3f)

    Row(
        Modifier
            .padding(top = 10.dp, bottom = 10.dp)
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .widthIn(min = 120.dp)
            .debounceClickable { if (isEnabled) refreshButtonPressed?.invoke() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_refresh),
            color = textColor,
            style = com.jar.app.core_compose_ui.theme.JarTypography.body1,
        )
        if (!isEnabled)
            Text(
                ((ticks).toInt()).secondsToMinsAndSecs(),
                color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2),
                modifier = Modifier.padding(start = 10.dp)
            )
    }
}

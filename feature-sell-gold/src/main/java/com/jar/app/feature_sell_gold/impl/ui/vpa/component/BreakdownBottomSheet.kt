package com.jar.app.feature_sell_gold.impl.ui.vpa.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jar.app.base.util.volumeToString
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_2E2942
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_534A71
import com.jar.app.core_compose_ui.component.JarCommonBoldText
import com.jar.app.core_compose_ui.component.JarCommonText
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_ui.R
import com.jar.app.feature_sell_gold.shared.MR.strings.amount_to_be_received
import com.jar.app.feature_sell_gold.shared.MR.strings.breakdown_gold_sell_price
import com.jar.app.feature_sell_gold.shared.MR.strings.feature_sell_gold_gold_quantity
import com.jar.app.feature_sell_gold.shared.MR.strings.okay_ack
import com.jar.app.feature_sell_gold.shared.MR.strings.view_price_breakdown

@Composable
fun BreakdownBottomSheet(
    goldSellPrice: Float,
    volumeFromAmount: Float,
    withdrawalPrice: String,
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 308.dp)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(modifier = Modifier.padding(top = 4.dp)) {
            JarCommonBoldText(
                modifier = Modifier.weight(1f),
                text = stringResource(id = amount_to_be_received.resourceId),
                style = JarTypography.h5,
                color = Color.White
            )
            IconButton(
                onClick = onCloseClick,
                modifier = Modifier.size(24.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.core_ui_ic_cross_outline),
                    contentDescription = null
                )
            }
        }
        Spacer(modifier = Modifier.size(24.dp))
        Column(
            modifier = Modifier
                .background(
                    color = colorResource(id = color_2E2942.resourceId),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val goldWeight by remember { mutableStateOf(volumeFromAmount) }
            BreakdownRowItem(
                stringResource(id = view_price_breakdown.resourceId),
                "₹$withdrawalPrice",
                true
            )
            DashLineSeparator()
            BreakdownRowItem(
                titleText = stringResource(id = feature_sell_gold_gold_quantity.resourceId),
                amountText = if (goldWeight >= 1.0) {
                    "${goldWeight.volumeToString()} g"
                } else {
                    "${goldWeight.volumeToString()} mg"
                }
            )
            BreakdownRowItem(
                stringResource(id = breakdown_gold_sell_price.resourceId),
                "₹$goldSellPrice/gm"
            )
        }
        Spacer(modifier = Modifier.size(24.dp))
        JarPrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = okay_ack.resourceId),
            onClick = onCloseClick,
            isAllCaps = false
        )
    }
}

@Composable
private fun BreakdownRowItem(titleText: String, amountText: String, isAmountBold: Boolean = false) {
    Row {
        JarCommonText(
            text = titleText,
            style = JarTypography.body2,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        JarCommonBoldText(
            text = amountText,
            style = JarTypography.body2.copy(
                fontWeight = if (isAmountBold) FontWeight.Bold else FontWeight.Normal
            ),
            color = Color.White
        )
    }
}

@Composable
private fun DashLineSeparator(modifier: Modifier = Modifier) {
    val dashColor = colorResource(id = color_534A71.resourceId)
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = dashColor,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2f
                )
            }
    )
}
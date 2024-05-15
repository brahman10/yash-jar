package com.jar.app.feature_lending.impl.ui.realtime_flow.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.RealTimeUiStep

@Composable
fun RealTimeFlowSteps(
    modifier: Modifier = Modifier,
    realTimeUiSteps: List<RealTimeUiStep> = arrayListOf(
        RealTimeUiStep(painterResource(id = R.drawable.feature_lending_bank_icon_36dp), "Link bank\naccount"),
        RealTimeUiStep(painterResource(id = R.drawable.feature_lending_cash_icon), "Get cash & \npay on schedule"),
        RealTimeUiStep(painterResource(id = R.drawable.feature_lending_credit_meter), "Improve Credit\nScore"),
    )
) {
    Box(
        modifier = modifier
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            realTimeUiSteps.forEachIndexed { index, step ->
                RealTimeFlowStep(
                    modifier = Modifier.weight(1f),
                    text = step.text,
                    showDividerRight = index != realTimeUiSteps.size - 1,
                    showDividerLeft = index != 0,
                    model = step.model
                )
            }
        }
    }
}

@Preview
@Composable
fun previewRealTimeFlowSteps() {
    RealTimeFlowSteps()
}
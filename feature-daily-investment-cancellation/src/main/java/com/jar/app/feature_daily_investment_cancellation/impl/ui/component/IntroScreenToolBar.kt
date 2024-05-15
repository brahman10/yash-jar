package com.jar.app.feature_daily_investment_cancellation.impl.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jar.app.core_compose_ui.component.RenderImagePillButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.views.RenderBaseToolBar
import com.jar.app.core_ui.R
import com.jar.app.feature_daily_investment_cancellation.shared.MR

@Composable
fun IntroScreenToolBar(RightSectionClick: () -> Unit, backPress: () -> Unit) {
    Column {
        RenderBaseToolBar(
            modifier = Modifier.background(colorResource(id = R.color.color_272239)),
            onBackClick = {
                backPress()
            },
            title = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.daily_saving),
            {
                RenderImagePillButton(
                    text = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.faq),
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .debounceClickable { RightSectionClick() },
                    drawableRes = com.jar.app.feature_daily_investment_tempering.R.drawable.question_mark,
                    bgColor = R.color.color_272239,
                    cornerRadius = 12.dp,
                    borderColor = R.color.color_3C3357,
                    textColor = com.jar.app.feature_daily_investment_tempering.R.color.pink_white
                )
            }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0x1AACA1D3))
        ) {

        }
    }
}
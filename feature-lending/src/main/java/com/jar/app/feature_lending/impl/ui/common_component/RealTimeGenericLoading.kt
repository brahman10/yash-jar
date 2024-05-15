package com.jar.app.feature_lending.impl.ui.common_component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_ui.R

@Preview
@Composable
fun RealTimeGenericLoading(
    modifier: Modifier = Modifier,
    message: String = ""
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .wrapContentSize()
                .align(Alignment.Center)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(88.dp)
                    .align(Alignment.CenterHorizontally),
                color = colorResource(id = R.color.color_EEEAFF),
                strokeWidth = 8.dp,
                backgroundColor = colorResource(id = R.color.color_776e94),
                strokeCap = StrokeCap.Round
            )
            if (message.isNotEmpty())
                Text(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    text = message,
                    style = JarTypography.h6,
                    color = colorResource(id = R.color.white),
                    lineHeight = 24.sp,
                    fontSize = 16.sp,
                    fontWeight = FontWeight(700),
                    textAlign = TextAlign.Center
                )
        }
    }
}
package com.jar.app.feature_daily_investment_cancellation.impl.ui.daily_investment_status_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarColors
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.feature_daily_investment_tempering.R


@Preview()
@Composable
private fun ScreenLayout() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JarColors.bgColor)
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(Modifier.fillMaxWidth().weight(1f))
            InfoBlock(
                image = R.drawable.stop_watch_ok,
                heading = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.we_are_facing_technical_issue),
                subText = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.please_try_again_after_some_time),
            )
            Spacer(Modifier.fillMaxWidth().weight(2f))
        }

        Box(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .align(Alignment.BottomCenter)
        ) {
            Column {
                NotificationBlock {

                }

                JarPrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                        .padding(start = 16.dp)
                        .padding(end = 16.dp),
                    text = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.retry),
                    isAllCaps = false,
                    onClick = {

                    }
                )
            }
        }
    }

}

@Composable
fun NotificationBlock(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .padding(top = 40.dp)
            .fillMaxWidth()
            .background(colorResource(id = com.jar.app.core_ui.R.color.color_2E2942), RoundedCornerShape(12.dp)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 17.dp)
                .padding(top = 17.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(painter = painterResource(R.drawable.notification_bell),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.Top)
                    .padding(start = 13.dp)
                    .debounceClickable { onClick() })
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.we_notify_you_in_24_hours),
                fontFamily = jarFontFamily,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.W400,
                color = colorResource(id = com.jar.app.core_ui.R.color.color_EEEAFF)
            )

        }
        Image(
            painter = painterResource(R.drawable.cross_small),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 6.dp)
                .padding(top = 6.dp)
        )
    }
}

@Composable
fun InfoBlock(image: Int, heading: String, subText: String, highlightedText: String = "") {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = ""
        )

        Spacer(modifier = Modifier.height(37.dp))

        Text(
            modifier = Modifier.wrapContentSize(),
            textAlign = TextAlign.Center,
            text = heading,
            fontFamily = jarFontFamily,
            lineHeight = 26.sp,
            fontWeight = FontWeight.W700,
            color = Color.White,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = generateSpanned(subText, " $highlightedText"),
            textAlign = TextAlign.Center,
            fontFamily = jarFontFamily,
            lineHeight = 20.sp,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp
        )
    }
}

private fun generateSpanned(text: String, highlightedText: String): AnnotatedString {
    return buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color(0xFFACA1D3))) {
            append(text)
        }
        withStyle(style = SpanStyle(color = Color.White)) {
            append(highlightedText)
        }
    }
}

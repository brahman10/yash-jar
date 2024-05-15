package com.jar.app.feature_daily_investment_cancellation.impl.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_compose_ui.component.JarImage
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.frauncesFontFamily
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentPostCancellationData
import com.jar.app.feature_daily_investment_tempering.R

@Composable
fun NotificationBlock(tittle: String) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .padding(top = 40.dp)
            .fillMaxWidth()
            .background(colorResource(id = com.jar.app.core_ui.R.color.color_2E2942), RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 17.dp)
                .padding(top = 17.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = painterResource(R.drawable.cross_white),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .padding(start = 13.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = tittle,
                fontFamily = jarFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                color = colorResource(id = com.jar.app.core_ui.R.color.color_EEEAFF)
            )
        }
    }
}

@Composable
fun PostCancellationTextBlock(
    text1: AnnotatedString, text2: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = com.jar.app.core_ui.R.color.color_272239))
            .padding(start = 16.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = text1,
            color = Color.White,
            fontFamily = frauncesFontFamily,
            fontSize = 24.sp,
            fontWeight = FontWeight.W700
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = text2,
            fontFamily = jarFontFamily,
            color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2),
            fontSize = 16.sp,
            fontWeight = FontWeight.W400
        )
    }
}

@Preview
@Composable
fun PostCancellationButtonBlockw() {
    PostCancellationButtonBlock({}, {}, {}, DailyInvestmentPostCancellationData())
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PostCancellationButtonBlock(
    saveWeeklyBtn: () -> Unit,
    buyGoldBtn: () -> Unit,
    homeBtn: () -> Unit,
    cancellationScreenData: DailyInvestmentPostCancellationData
) {
    val isSmallScreen = LocalConfiguration.current.screenWidthDp < 600
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = com.jar.app.core_ui.R.color.color_272239))
            .padding(bottom = 34.dp)
            .padding(start = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isSmallScreen) Modifier.weight(1f) else Modifier
                ),
            contentAlignment = Alignment.BottomEnd
        ) {
            JarImage(
                modifier = Modifier
                    .padding(top = 33.dp, start = 90.dp)
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit,
                imageUrl = BaseConstants.ImageUrlConstants.STOP_LOCKER,
                contentDescription = ""
            )
        }
        Row(
            modifier = Modifier
                .padding(end = 16.dp)
        ) {
            JarPrimaryButton(modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
                text = cancellationScreenData.saveButtonText.orEmpty(),
                elevation = 0.dp,
                fontWeight = FontWeight.W600,
                isAllCaps = false,
                color = colorResource(id = com.jar.app.core_ui.R.color.color_3C3357),
                borderBrush = null,
                onClick = {
                    saveWeeklyBtn()
                })

            Spacer(modifier = Modifier.width(16.dp))

            JarPrimaryButton(modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
                elevation = 0.dp,
                fontWeight = FontWeight.W600,
                isAllCaps = false,
                color = colorResource(id = com.jar.app.core_ui.R.color.color_6038CE),
                text = cancellationScreenData.buyButtonText.orEmpty(),
                onClick = {
                    buyGoldBtn()
                })
        }

        Spacer(modifier = Modifier.height(26.dp))

        Row(
            modifier = Modifier
                .padding(end = 16.dp)
                .fillMaxWidth()
                .debounceClickable(onClick = {
                    homeBtn()
                }),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.home),
                contentDescription = ""
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = cancellationScreenData.footerText.orEmpty(),
                fontFamily = jarFontFamily,
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.W600
            )
        }
    }
}
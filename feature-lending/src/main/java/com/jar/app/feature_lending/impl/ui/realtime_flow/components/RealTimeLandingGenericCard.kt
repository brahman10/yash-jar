package com.jar.app.feature_lending.impl.ui.realtime_flow.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.RealTimeGenericCard

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RealTimeLandingGenericCard(
    modifier: Modifier = Modifier,
    genericCard: RealTimeGenericCard
) {
    Box(
        modifier = modifier
            .background(
                Color(genericCard.backgroundColor.toColorInt()),
                RoundedCornerShape(8.dp)
            )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                GlideImage(
                    modifier = Modifier
                        .widthIn(min = 92.dp, max = 108.dp)
                        .height(72.dp),
                    model = genericCard.imageUrl,
                    contentDescription = null
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                ) {
                    Text(
                        text = genericCard.title,
                        style = JarTypography.h4,
                        lineHeight = 26.sp,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEEEAFF),

                        )
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = genericCard.description,
                        lineHeight = 18.sp,
                        fontSize = 12.sp,
                        color = Color(0xFFD5CDF2),
                        fontFamily = jarFontFamily
                    )

                }

            }
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.feature_lending_bg_divider_trust_brand),
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = genericCard.footerText,
                style = JarTypography.h4,
                lineHeight = 18.sp,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFFEEEAFF)
            )

        }


    }


}

@Preview
@Composable
fun previewCreditScoreCouldNotBeFoundCard() {
    RealTimeLandingGenericCard(
        genericCard = RealTimeGenericCard(
            backgroundColor = "#2E2942",
            title = "Your credit score could\nnot be found.",
            description = "You have no \nless credit history.",
            footerText = "Start building & improving it with Jar Cash.",
            imageUrl = "https://d21tpkh2l1zb46.cloudfront.net/UICards/Lending/Homecard.png"
        )
    )
}
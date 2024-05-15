package com.jar.app.feature_lending.impl.ui.realtime_flow.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_ui.R
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.BenefitsCard

@Composable
fun RealTimeLandingBenefitsCard(
    modifier: Modifier = Modifier,
    benefitsCard: BenefitsCard
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colorResource(id = R.color.lightBgColor),
                shape = RoundedCornerShape(CornerSize(8.dp))
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            text = benefitsCard.title,
            style = JarTypography.h5,
            color = Color(0xFFEEEAFF),
            lineHeight = 24.sp,
            fontSize = 16.sp,
            fontWeight = FontWeight(700),
            textAlign = TextAlign.Center
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            benefitsCard.realTimeBenefits.forEach { item ->
                BenefitsCardItem(
                    modifier = Modifier.weight(1f),
                    text = item.description,
                    imageUrl = item.imageUrl
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BenefitsCardItem(
    modifier: Modifier = Modifier,
    text: String,
    imageUrl: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(92.dp)
                .background(
                    Color(0xFF3C3357),
                    RoundedCornerShape(CornerSize(24.dp))
                )
        ) {
            GlideImage(
                modifier = Modifier.align(Alignment.Center),
                model = imageUrl,
                contentDescription = "",
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier.width(92.dp),
            text = text,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            style = JarTypography.body2,
            color = Color(0xFFEEEAFF),
            textAlign = TextAlign.Center
        )
    }


}

@Preview
@Composable
fun previewBenefitsCardItem() {
    BenefitsCardItem(text = "Lower\n interest rates", imageUrl = "gg", modifier = Modifier)
}

@Preview
@Composable
fun previewBenefitsCards() {

}

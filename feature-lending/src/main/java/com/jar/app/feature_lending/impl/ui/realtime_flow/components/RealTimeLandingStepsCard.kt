package com.jar.app.feature_lending.impl.ui.realtime_flow.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.CardItemData
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.StepsCard

@Composable
fun RealTimeLandingStepsCard(
    modifier: Modifier = Modifier,
    stepsCard: StepsCard
) {

    Column(
        modifier = modifier
            .background(
                color = colorResource(id = com.jar.app.core_ui.R.color.color_2E2942),
                shape = RoundedCornerShape(size = 8.dp)
            )
            .padding(8.dp)
    ) {
        Text(
            modifier= Modifier
                .padding(all = 16.dp)
                .align(Alignment.CenterHorizontally),
            text = stepsCard.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            style = JarTypography.h5,
            color = colorResource(id = com.jar.app.core_ui.R.color.color_EEEAFF),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.fillMaxWidth().height(16.dp))
        stepsCard.realTimeSteps.forEachIndexed { index, step ->
            RealTimeLandingCardStepItem(
                text = step.description,
                illustrationUrl = step.imageUrl,
                isLastStep = stepsCard.realTimeSteps.size == index + 1
            )
            if (index < stepsCard.realTimeSteps.size - 1) {
                DashedVerticalLine(modifier = Modifier.padding(start = 32.dp))
            }
        }
        Spacer(modifier = Modifier.fillMaxWidth().height(16.dp))
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RealTimeLandingCardStepItem(
    modifier: Modifier = Modifier,
    text: String,
    illustrationUrl: String,
    isLastStep: Boolean = false
) {

    Row(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = illustrationUrl,
            contentDescription = null,
            modifier = Modifier
                .size(36.dp)
                .aspectRatio(1f),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(24.dp))
        Text(
            modifier = modifier
                .fillMaxWidth(),
            text = text,
            color = Color.White,
            textAlign = TextAlign.Left,
            style = JarTypography.body2
        )
    }
}

@Preview
@Composable
fun RealTimeLandingCardStepItemPreview() {
    RealTimeLandingCardStepItem(
        text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_back_to_home.resourceId),
        illustrationUrl = "",
        isLastStep = false
    )
}

@Preview
@Composable
fun RealTimeLandingStepsCard() {
    val list = listOf<CardItemData>(
        CardItemData(
            "https://d21tpkh2l1zb46.cloudfront.net/UICards/Lending/Homecard.png",
            "Link your bank account to get approved"
        ),
        CardItemData(
            "https://d21tpkh2l1zb46.cloudfront.net/UICards/Lending/Homecard.png",
            "Unlock your personalised cash limit & get instant cash"
        ),
        CardItemData(
            "https://d21tpkh2l1zb46.cloudfront.net/UICards/Lending/Homecard.png",
            "Repay the amount taken as per schedule"
        )
    )
    RealTimeLandingStepsCard(
       stepsCard =  StepsCard(
            title = "Money in your bank account, easily!",
            realTimeSteps = list
        )
    )
}
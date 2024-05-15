package com.jar.app.feature_lending.impl.ui.credit_report.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.feature_lending.shared.domain.model.creditReport.RealTimeBenefits

@Composable
fun CheckCreditScoreImageCard(
    text: String
) {
    Column(
        modifier = Modifier
            .background(
                color = colorResource(id = com.jar.app.core_ui.R.color.bgColor),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(top = 32.dp)
            .fillMaxWidth()
    ) {
        Image(
            modifier = Modifier
                .size(width = 152.dp, height = 120.dp)
                .align(alignment = Alignment.CenterHorizontally),
            painter = painterResource(id = com.jar.app.feature_lending.R.drawable.feature_lending_bg_credit_score),
            contentDescription = ""
        )
        Text(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 24.dp)
                .fillMaxWidth(),
            text = text,
            style = JarTypography.h6.copy(
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = com.jar.app.core_ui.R.color.bottom_tab_active)
            ),
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }

}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CheckCreditScorePointsCard(data: RealTimeBenefits) {
    Row(
        modifier = Modifier
            .background(color = colorResource(id = com.jar.app.core_ui.R.color.color_2E2942))
            .fillMaxWidth()

    ) {

        GlideImage(
            modifier = Modifier
                .wrapContentWidth()
                .width(24.dp)
                .align(Alignment.CenterVertically),
            model = "${data.imageUrl}",
            contentDescription = data.description,

            )
        Text(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .wrapContentWidth()
                .align(alignment = Alignment.CenterVertically),
            text = data.description.orEmpty(),
            style = JarTypography.body1.copy(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = colorResource(id = com.jar.app.core_ui.R.color.white)
            ),
            maxLines = 2
        )
    }
}
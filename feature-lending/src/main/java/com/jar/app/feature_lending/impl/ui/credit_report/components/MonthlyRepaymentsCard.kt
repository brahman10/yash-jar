package com.jar.app.feature_lending.impl.ui.credit_report.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.feature_lending.shared.domain.model.creditReport.CreditDetailedReportResponse

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MonthlyRepaymentsCard(
    creditDetailedReportData: CreditDetailedReportResponse?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        creditDetailedReportData?.title?.let {
            Text(
                modifier = Modifier
                    .padding(top = 40.dp, bottom = 12.dp)
                    .fillMaxWidth(),
                text = it,
                style = JarTypography.h2.copy(
                    fontSize = 28.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color((creditDetailedReportData.titleColor?:"#FFFFFF").toColorInt()  )
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .background(
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_2E2942),
                    shape = RoundedCornerShape(42.dp)
                )
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center

        ) {
            GlideImage(
                modifier = Modifier.width(24.dp)
                    .align(alignment = Alignment.CenterVertically),
                model = creditDetailedReportData?.subTitleIcon.orEmpty(),
                contentDescription = "",
                contentScale = ContentScale.Fit
            )
            Text(
                modifier = Modifier
                    .padding(vertical = 12.dp, horizontal = 4.dp)
                    .wrapContentWidth()
                    .align(alignment = Alignment.CenterVertically),
                text = creditDetailedReportData?.subTitle.orEmpty(),
                style = JarTypography.body1.copy(
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2)
                )
            )
        }
        Text(
            modifier = Modifier
                .padding(top = 20.dp, bottom = 40.dp)
                .fillMaxWidth(),
            text = creditDetailedReportData?.description.orEmpty(),
            style = JarTypography.body1.copy(
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = colorResource(id = com.jar.app.core_ui.R.color.smallTxtColor)
            ),
            textAlign = TextAlign.Center
        )
    }
}
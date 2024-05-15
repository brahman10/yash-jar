package com.jar.app.feature_lending.impl.ui.credit_report.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.feature_lending.shared.domain.model.creditReport.Factors
import com.jar.app.feature_lending.shared.MR
@Composable
fun CellCreditScoreHeaderCard(data:List<Factors>
) {
    Column(  modifier = Modifier
        .padding(top = 24.dp,start = 16.dp,end =16.dp, bottom = 16.dp)
        .background(
            color = colorResource(id = com.jar.app.core_ui.R.color.color_2E2942),
            shape = RoundedCornerShape(8.dp)
        )
        .fillMaxWidth()
        .padding(start = 16.dp,end = 16.dp,bottom = 4.dp)
    ) {

            Text(
                modifier = Modifier.padding(top = 20.dp, bottom = 8.dp).fillMaxWidth(),
                text = stringResource(id =MR.strings.feature_lending_factors_impacting_title.resourceId),
                style = JarTypography.h2.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = com.jar.app.core_ui.R.color.commonTxtColor),
                ),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        data.let {
            it.forEachIndexed { _, detail ->
                CellCreditScoreFactorCard(creditScoreImpactData = detail)
            }
        }
    }

}
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CellCreditScoreFactorCard(
    creditScoreImpactData: Factors) {
    Row(
        modifier = Modifier
            .background(
                color = colorResource(id = com.jar.app.core_ui.R.color.color_2E2942),

                )
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {

        Row(modifier = Modifier.weight(1f)) {
            GlideImage(
                modifier = Modifier.size(28.dp)
                    .align(Alignment.Top),
                model = "${creditScoreImpactData.icon}",
                contentDescription = "",

            )
            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text(
                    text = creditScoreImpactData.name.orEmpty(),
                    style = JarTypography.h2.copy(
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(id = com.jar.app.core_ui.R.color.white)
                    ),
                    maxLines = 2,
                )
                Text(
                    text = creditScoreImpactData.status.orEmpty(),
                    style = JarTypography.body1.copy(
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        color = colorResource(id = com.jar.app.core_ui.R.color.smallTxtColor)
                    )
                )
            }
        }
        Text(
            modifier = Modifier.weight(1f),
            text = creditScoreImpactData.description.orEmpty(),
            style = JarTypography.body1.copy(
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2),
            ),
            maxLines = 3
        )
    }
}

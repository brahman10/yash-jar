package com.jar.app.feature_lending.impl.ui.credit_report.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.jar.app.core_compose_ui.component.noRippleDebounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_ui.R
import com.jar.app.feature_lending.shared.domain.model.creditReport.Performance

@Composable
fun CreditLimitAndUsageCard(
    performanceData: Performance,
    inPerformanceCardClicked: (data:Performance) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(top = 12.dp,start = 16.dp,end = 16.dp)
            .background(
                color = colorResource(id = R.color.bgColor),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                2.dp,
                colorResource(id = R.color.color_846FC0),
                shape = RoundedCornerShape(8.dp)
            )
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp).noRippleDebounceClickable { inPerformanceCardClicked(performanceData) }
    ) {
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
                .fillMaxWidth()
        ) {
            Text(
                text = performanceData.name.orEmpty(),
                style = JarTypography.h2.copy(
                    fontSize = 18.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.color_EEEAFF)
                ),
                maxLines = 2,
            )
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = performanceData.impact.orEmpty(),
                style = JarTypography.h2.copy(
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(id = R.color.smallTxtColor)
                )
            )
        }
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = performanceData.status.orEmpty(),
            style = JarTypography.h2.copy(
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color((performanceData.statusColor?:"#FFFFFF").toColorInt())
            )
        )
        Image(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 16.dp),
            painter = painterResource(id = com.jar.app.feature_lending.R.drawable.feature_lending_ic_chevron),
            contentDescription = ""
        )
    }
}
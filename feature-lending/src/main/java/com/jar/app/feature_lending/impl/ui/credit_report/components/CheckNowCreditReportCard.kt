package com.jar.app.feature_lending.impl.ui.credit_report.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.component.JarSecondaryButton
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.feature_lending.shared.MR

@Composable
fun CheckNowCreditReportCard(
    lastUpdatedOn: String,
    onCheckNowClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(top = 28.dp, start = 16.dp, end = 16.dp)
            .background(
                color = colorResource(id = com.jar.app.core_ui.R.color.color_2E2942),
                shape = RoundedCornerShape(8.dp)
            )

            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = MR.strings.feature_lending_credit_report_available.resourceId),
                style = JarTypography.body1.copy(
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_EEEAFF)
                ),
                maxLines = 2,
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = stringResource(id = MR.strings.feature_lending_last_updated.resourceId,lastUpdatedOn),
                style = JarTypography.body1.copy(
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3)
                )
            )
        }
        JarSecondaryButton(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            text = stringResource(id = MR.strings.feature_lending_check_now.resourceId),
            isAllCaps = false,
            minHeight = 36.dp,
            onClick = {
                onCheckNowClick()
            }
        )

    }
}
@Preview
@Composable
fun CheckNowCreditReportCarda(){
    CheckNowCreditReportCard("Hello",{})
}
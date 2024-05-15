package com.jar.app.feature_lending.impl.ui.credit_report.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.component.noRippleDebounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.feature_lending.shared.domain.model.creditReport.CreditDetailedReportResponse
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.model.creditReport.CreditCardsAndLoan

@Composable
fun RepaymentHistoryTab(
    creditDetailedReportData: CreditDetailedReportResponse?, onTabClick: (Boolean) -> Unit = {}
) {
    val creditText: String =
        stringResource(id = MR.strings.feature_lending_credit_card_title.resourceId,creditDetailedReportData?.creditCardsList?.size.orZero())
    val loanText: String =
        stringResource(id = MR.strings.feature_lending_loan_account_title.resourceId,creditDetailedReportData?.loanAccountsList?.size.orZero())
    var isSelected by remember { mutableStateOf(false) }
    Column {
        Column(
            modifier = Modifier
                .background(
                    color = colorResource(id = com.jar.app.core_base.shared.CoreBaseMR.colors.color_3C3357.resourceId),
                    shape = RoundedCornerShape(12.dp, 12.dp, 0.dp, 0.dp)
                )
                .fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    modifier = (if (!isSelected) Modifier.background(
                        color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
                        shape = RoundedCornerShape(4.dp),
                    ) else Modifier)
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 4.dp, vertical = 10.dp)
                        .weight(1f)
                        .fillMaxWidth()
                        .noRippleDebounceClickable {
                            isSelected = false
                            onTabClick(isSelected)
                        },

                    textAlign = TextAlign.Center,
                    text = creditText,
                    style = if (!isSelected) JarTypography.body1.copy(
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(id = com.jar.app.core_ui.R.color.color_272239)
                    ) else JarTypography.body1.copy(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = colorResource(id = com.jar.app.core_ui.R.color.color_EEEAFF)
                    ),
                    maxLines = 1,
                )
                Text(
                    modifier = (if (isSelected) Modifier.background(
                        color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
                        shape = RoundedCornerShape(4.dp),
                    ) else Modifier)
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 4.dp, vertical = 10.dp)
                        .weight(1f)
                        .fillMaxWidth()
                        .noRippleDebounceClickable {
                            isSelected = true
                            onTabClick(isSelected)
                        },
                    textAlign = TextAlign.Center,
                    text = loanText,
                    style = if (isSelected) JarTypography.body1.copy(
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(id = com.jar.app.core_ui.R.color.color_272239)
                    ) else JarTypography.body1.copy(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = colorResource(id = com.jar.app.core_ui.R.color.color_EEEAFF)
                    ),
                    maxLines = 1,
                )
            }
            Divider(
                color = colorResource(id = com.jar.app.core_base.shared.CoreBaseMR.colors.color_20_776E94.resourceId),
                thickness = 2.dp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        val data: List<CreditCardsAndLoan>? = if (!isSelected) {
            creditDetailedReportData?.creditCardsList
        } else {
            creditDetailedReportData?.loanAccountsList
        }
        if (data.isNullOrEmpty()) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
                Text(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth(),
                    text = if (!isSelected) stringResource(id = MR.strings.feature_lending_credit_accounts_unavailable.resourceId) else
                        stringResource(id = MR.strings.feature_lending_loan_accounts_details_unavailable.resourceId),
                    style = JarTypography.h6.copy(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = colorResource(id = com.jar.app.core_ui.R.color.commonTxtColor)
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Text(
                    modifier = Modifier
                        .padding(top = 12.dp, start = 10.dp,end=10.dp)
                        .fillMaxWidth(),
                    text = if (!isSelected) stringResource(id = MR.strings.feature_lending_credit_details_unavailable.resourceId) else
                        stringResource(id = MR.strings.feature_lending_loan_details_unavailable.resourceId),
                    style = JarTypography.body1.copy(
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2)
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }

        } else {
            data.forEachIndexed { _, creditCardsAndLoan ->
                CreditAccountsCard(creditCardsData = creditCardsAndLoan)
            }
        }
    }
}
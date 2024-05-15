package com.jar.app.feature_lending.impl.ui.credit_report.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.base.util.orFalse
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_ui.R
import com.jar.app.feature_lending.shared.domain.model.creditReport.AccountDetails
import com.jar.app.feature_lending.shared.domain.model.creditReport.CreditCardsAndLoan
import com.jar.app.feature_lending.shared.MR

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CreditAccountsCard(
    creditCardsData: CreditCardsAndLoan
) {
    Column(
        modifier = Modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .background(
                color = colorResource(
                    id = if (creditCardsData.accountDetails?.isActive.orFalse())
                        R.color.purple400
                    else R.color.color_2E2942
                ), shape = RoundedCornerShape(8.dp)
            )
            .fillMaxWidth()
    ) {
        Row {
            GlideImage(
                modifier = Modifier
                    .align(Alignment.Top)
                    .padding(start = 16.dp, top = 16.dp, end = 8.dp)
                    .size(36.dp),
                model = creditCardsData.accountDetails?.imageUrl.orEmpty(),
                contentDescription = "",

                )
            Column {
                Row(
                    modifier = Modifier
                        .padding(end = 16.dp, top = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = if (creditCardsData.accountDetails?.status?.isNotEmpty() == true) {
                            Modifier.weight(1f)
                        } else Modifier,
                        text = creditCardsData.accountDetails?.title.orEmpty(),
                        style = JarTypography.h2.copy(
                            fontSize = 20.sp,
                            lineHeight = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (creditCardsData.accountDetails?.isActive.orFalse())
                                colorResource(id = R.color.color_EEEAFF) else
                                colorResource(id = R.color.color_ACA1D3)
                        ),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                    if (creditCardsData.accountDetails?.status?.isNotEmpty() == true) {
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                            text = creditCardsData.accountDetails?.status.orEmpty(),
                            style = JarTypography.body1.copy(
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                color = Color(
                                    (creditCardsData.accountDetails?.statusColor
                                        ?: "#FFFFFF").toColorInt()
                                ),
                                textAlign = TextAlign.Right
                            )
                        )
                    }
                }
                creditCardsData.accountDetails.let {
                    extractedColumn(creditCardsData.accountDetails)
                }

            }
        }
        Divider(
            color = colorResource(id = com.jar.app.core_base.shared.CoreBaseMR.colors.color_20_776E94.resourceId),
            thickness = 2.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        creditCardsData.detailsList.let {
            it?.forEachIndexed { _, detail ->
                Row(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = detail.key.orEmpty(),
                        style = JarTypography.body1.copy(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = colorResource(
                                id = if (creditCardsData.accountDetails?.isActive.orFalse())
                                    R.color.color_EEEAFF
                                else R.color.color_ACA1D3
                            )
                        ),
                    )
                    Text(
                        text = detail.value.orEmpty(),
                        modifier = Modifier.align(Alignment.CenterVertically),
                        style = JarTypography.body1.copy(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = colorResource(
                                id = if (creditCardsData.accountDetails?.isActive.orFalse())
                                    R.color.color_EEEAFF
                                else R.color.color_ACA1D3
                            ),
                            textAlign = TextAlign.Right,
                        )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun extractedColumn(creditAccountsData: AccountDetails?) {
    Row(
        modifier = Modifier
            .padding(end = 16.dp, top = 4.dp, bottom = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            modifier = Modifier
                .wrapContentWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = if (creditAccountsData?.subTitle.isNullOrBlank()) ""
                else creditAccountsData?.subTitle.orEmpty().plus(": "),
                style = JarTypography.body1.copy(
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = colorResource(id = R.color.color_ACA1D3),
                ),
            )
            Text(
                text = if (creditAccountsData?.isActive.orFalse())
                    stringResource(id = MR.strings.feature_lending_credit_active.resourceId)
                else stringResource(id = MR.strings.feature_lending_credit_closed.resourceId),
                style = JarTypography.body1.copy(
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = if (creditAccountsData?.isActive.orFalse())
                        colorResource(id = R.color.white) else
                        colorResource(id = R.color.color_ACA1D3)
                )
            )
        }
        Text(
            text = creditAccountsData?.paymentDate.orEmpty(),
            modifier = Modifier.align(Alignment.CenterVertically),
            style = JarTypography.body1.copy(
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = colorResource(id = R.color.color_ACA1D3),
                textAlign = TextAlign.Right
            )
        )
    }
}

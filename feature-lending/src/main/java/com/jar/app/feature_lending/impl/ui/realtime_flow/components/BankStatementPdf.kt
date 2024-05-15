package com.jar.app.feature_lending.impl.ui.realtime_flow.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.impl.domain.model.realTimeFlow.BankStatementPdfDetail

@Composable
fun BankStatementPdf(
    bankStatementPdfDetail: BankStatementPdfDetail,
    onCrossClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            Column(
                modifier = if (bankStatementPdfDetail.isFailed) {
                    Modifier
                        .background(
                            color = colorResource(id = com.jar.app.core_ui.R.color.color_2E2942),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            colorResource(id = com.jar.app.core_ui.R.color.color_EB6A6E),
                            shape = RoundedCornerShape(8.dp)
                        )
                } else Modifier.background(
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_2E2942),
                    shape = RoundedCornerShape(8.dp)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.feature_lending_real_time_flow_pdf_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(width = 40.dp, height = 48.dp)

                    )
                    Column() {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 20.dp),
                                text = bankStatementPdfDetail.name,
                                fontSize = 14.sp,
                                fontFamily = jarFontFamily,
                                fontWeight = FontWeight(600),
                                color = colorResource(id = com.jar.app.core_ui.R.color.color_EEEAFF),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, top = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = bankStatementPdfDetail.size,
                                fontSize = 12.sp,
                                fontFamily = jarFontFamily,
                                color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2)
                            )
                            if (bankStatementPdfDetail.isUploadSuccessful) {
                                Text(
                                    text = stringResource(com.jar.app.feature_lending.shared.MR.strings.feature_lending_upload_successful.resourceId),
                                    fontSize = 12.sp,
                                    fontFamily = jarFontFamily,
                                    color = colorResource(id = com.jar.app.core_ui.R.color.color_1ea787)
                                )

                            }


                        }

                    }
                }
            }
            if (bankStatementPdfDetail.isFailed) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.feature_lending_real_time_flow_alert_icon),
                        contentDescription = ""
                    )
                    Text(
                        modifier = Modifier.padding(start = 4.dp),
                        text = bankStatementPdfDetail.failedReason.orEmpty(),
                        fontSize = 12.sp,
                        fontFamily = jarFontFamily,
                        color = colorResource(id = com.jar.app.core_ui.R.color.color_EB6A6E)
                    )

                }
            }


        }
        if (bankStatementPdfDetail.showCrossButton) {
            Image(
                painter = painterResource(id = R.drawable.feature_lending_real_time_flow_cross_icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .align(Alignment.TopEnd)
                    .clickable { onCrossClick() }
            )
        }


    }

}

@Preview
@Composable
fun previewBankStatementPdf() {
    BankStatementPdf(
        bankStatementPdfDetail = BankStatementPdfDetail(
            name = "Robin Goyal-305-May",
            size = "1.28 MB",
            uri = Uri.EMPTY,
            isFailed = true,
            isUploadSuccessful = true,
            showCrossButton = true,
        ),
        onCrossClick = {}
    )
}
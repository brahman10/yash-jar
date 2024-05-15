package com.jar.app.feature_lending.impl.ui.realtime_flow_with_camps.pan.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.theme.jarInterFontFamily
import com.jar.app.core_ui.R
import com.jar.app.feature_lending.impl.ui.common_component.shimmerBrush

@Composable
fun IdentityDetailCard(
    modifier: Modifier = Modifier,
    panNumber: String,
    panHolderName: String,
    panHolderDOB: String
) {


        Column(
            modifier = modifier
                .border(
                    1.dp,
                    colorResource(id = R.color.color_776e94),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(8.dp)
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    color = colorResource(id = R.color.color_2E2942),
                    shape = RoundedCornerShape(20.dp)
                )
                .background(
                    brush = shimmerBrush(),
                    shape = RoundedCornerShape(20.dp)
                ),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_permanent_account_number.resourceId),
                    fontSize = 12.sp,
                    fontFamily = jarInterFontFamily,
                    color = colorResource(id = R.color.color_D5CDF2)
                )

                Text(
                    text = panNumber,
                    fontSize = 28.sp,
                    fontFamily = jarInterFontFamily,
                    fontWeight = FontWeight(700),
                    color = colorResource(id = R.color.color_EEEAFF)
                )
            }
            Divider(
                color = colorResource(id = R.color.color_3C3357), thickness = 1.dp
            )
            Row(modifier = Modifier.padding(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(id = R.string.core_ui_name_on_card),
                        fontSize = 12.sp,
                        fontFamily = jarInterFontFamily,
                        color = colorResource(id = R.color.color_D5CDF2)
                    )

                    Text(
                        text = panHolderName,
                        fontSize = 14.sp,
                        fontFamily = jarInterFontFamily,
                        fontWeight = FontWeight(400),
                        color = colorResource(id = R.color.color_EEEAFF)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = stringResource(id = R.string.core_ui_date_of_birth),
                        fontSize = 12.sp,
                        fontFamily = jarInterFontFamily,
                        color = colorResource(id = R.color.color_D5CDF2)
                    )

                    Text(
                        text = panHolderDOB,
                        fontSize = 14.sp,
                        fontFamily = jarInterFontFamily,
                        fontWeight = FontWeight(400),
                        color = colorResource(id = R.color.color_EEEAFF)
                    )
                }
            }
        }
}

@Preview
@Composable
fun IdentityDetailCardPreview() {
    IdentityDetailCard(
        modifier = Modifier.padding(8.dp),
        panNumber = "FGHCD2345L",
        panHolderName = "Mohammed Sulaimaan Khan Durrani",
        panHolderDOB = "20/02/1994"
    )
}
package com.jar.app.feature_lending.impl.ui.realtime_flow.bank_statement.upload

import android.text.SpannableStringBuilder
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.bold
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.core_compose_ui.utils.toAnnotatedString
import com.jar.app.core_ui.R
import com.jar.app.feature_lending.shared.domain.model.v2.BankAccount
import com.jar.app.feature_lending.impl.ui.realtime_flow.components.UploadBankStatementButton


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun UploadBankStatementComposable(
    modifier: Modifier = Modifier,
    bankDetail: BankAccount? = null,
    time: String,
    onSubmitClick: () -> Unit
) {


    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        bankDetail?.let {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.color_3E3953))
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    text = stringResource(
                        com.jar.app.feature_lending.shared.MR.strings.feature_lending_provide_bank_statement.resourceId,
                        bankDetail.bankName.orEmpty()
                    ),
                    fontSize = 20.sp,
                    fontFamily = jarFontFamily,
                    fontWeight = FontWeight(700),
                    color = colorResource(id = R.color.white)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp, start = 15.dp)
                ) {
                    GlideImage(
                        modifier = Modifier
                            .size(20.dp),
                        model = bankDetail.bankLogo,
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp),
                        text = bankDetail.bankName.orEmpty(),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontFamily = jarFontFamily,
                        fontWeight = FontWeight(400),
                        color = colorResource(id = R.color.white)
                    )
                }

            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 28.dp)
                .background(colorResource(id = R.color.color_2e2942), RoundedCornerShape(8.dp))
        ) {
            Image(
                modifier = Modifier.padding(
                    top = 16.dp,
                    bottom = 16.dp,
                    start = 12.dp,
                    end = 8.dp
                ),
                painter = painterResource(id = com.jar.app.feature_lending.R.drawable.feature_lending_real_time_flow_pdf_icon),
                contentDescription = ""
            )

            val labelText =
                SpannableStringBuilder().append(stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_submit_bank_statement.resourceId))
                    .bold { append(time) }
            Text(
                modifier = Modifier.padding(vertical = 16.dp),
                text = labelText.toAnnotatedString(),
                fontSize = 14.sp,
                fontFamily = jarFontFamily,
                color = colorResource(id = R.color.commonTxtColor),
                lineHeight = 20.sp
            )


        }
        UploadBankStatementButton(
            title = stringResource(
                id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_upload_bank_statement.resourceId,
            ),
            subTitle = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_only_pdf_files_are_allowed.resourceId),
            onClick = onSubmitClick
        )
    }


}

@Preview
@Composable
fun PreviewUploadBankStatementComposable() {
    UploadBankStatementComposable(
        bankDetail = BankAccount(bankName = "Axis Bank", bankLogo = ""),
        time = "6 months",
        onSubmitClick = {},
    )
}

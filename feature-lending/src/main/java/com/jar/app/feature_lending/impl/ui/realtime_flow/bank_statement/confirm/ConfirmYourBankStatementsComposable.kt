package com.jar.app.feature_lending.impl.ui.realtime_flow.bank_statement.confirm

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.impl.domain.model.realTimeFlow.BankStatementPdfDetail
import com.jar.app.feature_lending.impl.domain.model.realTimeFlow.CtaType
import com.jar.app.feature_lending.impl.ui.realtime_flow.components.BankStatementPdf
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi

@Composable
internal fun ConfirmYourBankStatementsFragmentComposable(
    modifier: Modifier = Modifier,
    bankStatementList: List<BankStatementPdfDetail>,
    uploadedBankStatementList: List<BankStatementPdfDetail>? = null,
    ctaType: CtaType,
    showWarning: Boolean,
    warningString: String,
    onCrossClick: (Uri, Int) -> Unit,
    showUploading: Boolean,
    showUploadSuccess: Boolean = false,
    showUploadError: Boolean = false,
    uploadPercent: String = "",
    analyticsApi: AnalyticsApi? = null
) {
    if (showUploading) {
        LaunchedEffect(key1 = Unit) {
            analyticsApi?.postEvent(
                LendingEventKeyV2.RLENDING_AFTERBANKSTATEMENTUPLOADFLOW,
                mapOf(
                    LendingEventKeyV2.screen_name to LendingEventKeyV2.UPLOAD_BANKSTATEMENT_SCREEN,
                    LendingEventKeyV2.action to LendingEventKeyV2.UPLOADING_BANKSTATEMENT_SCREEN_SHOWN
                )
            )
        }
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.padding(bottom = 16.dp)) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(88.dp)
                        .align(Alignment.Center),
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_EEEAFF),
                    strokeWidth = 8.dp,
                    backgroundColor = colorResource(id = com.jar.app.core_ui.R.color.color_776e94),
                    strokeCap = StrokeCap.Round
                )
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = uploadPercent,
                    fontSize = 18.sp,
                    fontFamily = jarFontFamily,
                    fontWeight = FontWeight(700),
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_EEEAFF),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_uploading_your_bank_statement.resourceId),
                fontSize = 18.sp,
                fontFamily = jarFontFamily,
                fontWeight = FontWeight(700),
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_it_may_take_a_few_seconds_to_upload.resourceId),
                fontSize = 14.sp,
                fontFamily = jarFontFamily,
                color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2),
                textAlign = TextAlign.Center
            )

        }

    } else if (showUploadSuccess || showUploadError) {
        if (showUploadSuccess) {
            LaunchedEffect(key1 = Unit) {
                analyticsApi?.postEvent(
                    LendingEventKeyV2.RLENDING_AFTERBANKSTATEMENTUPLOADFLOW,
                    mapOf(
                        LendingEventKeyV2.screen_name to LendingEventKeyV2.UPLOAD_BANKSTATEMENT_SCREEN,
                        LendingEventKeyV2.action to LendingEventKeyV2.UPLOAD_COMPLETED_SCREEN_SHOWN
                    )
                )
            }
        }
        if (showUploadError) {
            LaunchedEffect(key1 = Unit) {
                analyticsApi?.postEvent(
                    LendingEventKeyV2.RLENDING_AFTERBANKSTATEMENTUPLOADFLOW,
                    mapOf(
                        LendingEventKeyV2.screen_name to LendingEventKeyV2.BANK_STATEMENT_SCREEN,
                        LendingEventKeyV2.text_displayed to LendingEventKeyV2.error_message_failed_to_upload
                    )
                )
            }
        }
        Box(modifier = Modifier.padding(bottom = 16.dp)) {
            Column(
                modifier = modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = modifier
                        .size(56.dp),
                    painter = painterResource(
                        id = if (showUploadError) R.drawable.feature_leanding_ic_alert
                        else com.jar.app.core_ui.R.drawable.core_ui_ic_green_tick
                    ),
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.padding(top = 12.dp),
                    text = stringResource(
                        id = if (showUploadError) com.jar.app.feature_lending.shared.MR.strings.feature_lending_upload_failed.resourceId
                        else com.jar.app.feature_lending.shared.MR.strings.feature_lending_upload_completed.resourceId
                    ),
                    fontSize = 16.sp,
                    fontFamily = jarFontFamily,
                    color = colorResource(id = com.jar.app.core_ui.R.color.white),
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {

        LaunchedEffect(key1 = Unit) {
            analyticsApi?.postEvent(
                LendingEventKeyV2.RLENDING_AFTERBANKSTATEMENTUPLOADFLOW,
                mapOf(
                    LendingEventKeyV2.screen_name to LendingEventKeyV2.BANK_STATEMENT_CONFIRMATION_SCREEN,
                    LendingEventKeyV2.action to LendingEventKeyV2.max_file
                )
            )
        }
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
        ) {
            if (bankStatementList.isNotEmpty()) {
                item(key = "Header") {
                    Column(
                        modifier = Modifier
                    ) {
                        if (ctaType == CtaType.CONFIRM) {
                            Text(
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 20.dp,
                                    bottom = 12.dp
                                ),
                                text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_confirm_your_bank_statements.resourceId),
                                fontSize = 20.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight(700),
                                color = colorResource(id = com.jar.app.core_ui.R.color.white)
                            )
                        }
                        if (showWarning) {
                            LaunchedEffect(key1 = Unit) {
                                analyticsApi?.postEvent(
                                    LendingEventKeyV2.RLENDING_ERRORSCREENEVENT,
                                    mapOf(
                                        LendingEventKeyV2.screen_name to LendingEventKeyV2.BANK_STATEMENT_CONFIRMATION_SCREEN,
                                        LendingEventKeyV2.text_displayed to warningString
                                    )
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .background(Color.Transparent, RoundedCornerShape(8.dp))
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, bottom = 6.dp)
                                    .border(
                                        1.dp,
                                        colorResource(id = com.jar.app.core_ui.R.color.color_EB6A6E),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            ) {
                                Image(
                                    modifier = Modifier
                                        .padding(start = 12.dp, top = 16.dp, bottom = 16.dp)
                                        .align(Alignment.CenterVertically),
                                    painter = painterResource(id = R.drawable.feature_lending_real_time_flow_alert_icon),
                                    contentDescription = ""
                                )
                                Text(
                                    modifier = Modifier
                                        .padding(start = 4.dp, top = 16.dp, bottom = 16.dp)
                                        .align(Alignment.CenterVertically),
                                    text = warningString,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    color = colorResource(id = com.jar.app.core_ui.R.color.color_EB6A6E)
                                )
                            }
                        }

                    }
                }
            }
            itemsIndexed(
                items = bankStatementList,
                key = { index, _ -> index }) { index, statement ->
                BankStatementPdf(
                    bankStatementPdfDetail = statement,
                    onCrossClick = {
                        onCrossClick(statement.uri, index)
                        analyticsApi?.postEvent(
                            LendingEventKeyV2.RLENDING_AFTERBANKSTATEMENTUPLOADFLOW,
                            mapOf(
                                LendingEventKeyV2.screen_name to LendingEventKeyV2.BANK_STATEMENT_CONFIRMATION_SCREEN,
                                LendingEventKeyV2.action to LendingEventKeyV2.FILE_DELETED
                            )
                        )
                    }
                )

            }
//                    bankStatementList.forEachIndexed { index, statement ->
//                        if (statement.isFailed) {
//                            LaunchedEffect(key1 = Unit) {
//                                analyticsApi?.postEvent(
//                                    LendingEventKeyV2.RLENDING_AFTERBANKSTATEMENTUPLOADFLOW,
//                                    mapOf(
//                                        LendingEventKeyV2.screen_name to LendingEventKeyV2.BANK_STATEMENT_VIEW_SCREEN,
//                                        LendingEventKeyV2.text_displayed to statement.failedReason.orEmpty()
//                                    )
//                                )
//                            }
//                        }
//
//                    }
            uploadedBankStatementList?.let {
                item(key = "Uploaded") {
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
                        text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_uploade_files.resourceId),
                        fontSize = 20.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight(700),
                        color = colorResource(id = com.jar.app.core_ui.R.color.white)
                    )
                }
                itemsIndexed(
                    items = it,
                    key = { index, _ -> bankStatementList.size + index }) { index, statement ->
                    BankStatementPdf(
                        bankStatementPdfDetail = statement,
                        onCrossClick = {
                            onCrossClick(statement.uri, index)
                            analyticsApi?.postEvent(
                                LendingEventKeyV2.RLENDING_AFTERBANKSTATEMENTUPLOADFLOW,
                                mapOf(
                                    LendingEventKeyV2.screen_name to LendingEventKeyV2.BANK_STATEMENT_CONFIRMATION_SCREEN,
                                    LendingEventKeyV2.action to LendingEventKeyV2.FILE_DELETED
                                )
                            )
                        }
                    )
                }
            }
        }
    }


}

@Preview
@Composable
fun PreviewConfirmYourBankStatementsFragmentComposable() {

    ConfirmYourBankStatementsFragmentComposable(

        bankStatementList = listOf(
            BankStatementPdfDetail(
                Uri.parse("gg"),
                "Robin Goyal-305-May",
                ".28 MB",
            ),
        ),
        ctaType = CtaType.CONFIRM,
        showWarning = true,
        warningString = "You can upload a maximum of 4 pdfs",
        onCrossClick = { uri, index -> },
        showUploading = false,
        uploadPercent = "80"
    )
}


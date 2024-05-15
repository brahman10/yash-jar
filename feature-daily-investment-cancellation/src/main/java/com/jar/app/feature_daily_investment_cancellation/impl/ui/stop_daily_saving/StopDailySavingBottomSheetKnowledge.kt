package com.jar.app.feature_daily_investment_cancellation.impl.ui.stop_daily_saving

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.core_compose_ui.utils.generateSpannedFromHtmlString
import com.jar.app.core_compose_ui.utils.toAnnotatedString
import com.jar.app.feature_daily_investment_cancellation.impl.ui.component.AllUserLogo
import com.jar.app.feature_daily_investment_cancellation.impl.ui.component.DailySavingBenefitBox
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey.Button_type
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentStatusScreen
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentStopKey
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentConfirmActionDetails
import com.jar.app.feature_daily_investment_tempering.R
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi


@Composable
internal fun RenderStopDailySavingKnowledgeBottomSheet(
    stopDailySavingBottomSheetDetails: DailyInvestmentConfirmActionDetails,
    onDismissClick: () -> Unit,
    onContinueDailySavingClicked: () -> Unit,
    onStopDailySavingClicked: () -> Unit,
    analyticsHandler: AnalyticsApi
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                colorResource(id = com.jar.app.core_ui.R.color.color_2E2942),
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)

            )
    ) {
        Image(
            painter = painterResource(R.drawable.cross_without_boarder),
            contentDescription = "",
            modifier = Modifier
                .debounceClickable {
                    analyticsHandler.postEvent(
                        DailyInvestmentCancellationEventKey.DSCancellation_StopDSpopup_Clicked,
                        mapOf(
                            Button_type to DailyInvestmentStatusScreen.Close,
                            DailyInvestmentStopKey.type to DailyInvestmentStatusScreen.Knowledge
                        )
                    )
                    onDismissClick.invoke()
                }
                .padding(end = 19.dp, top = 19.dp)
                .align(Alignment.TopEnd)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.Start)
                    .fillMaxWidth(),
                painter = painterResource(id = com.jar.app.core_ui.R.drawable.lamp_logo),
                alignment = Alignment.TopStart,
                contentScale = ContentScale.FillWidth,
                contentDescription = ""
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val featureMap =
                    stopDailySavingBottomSheetDetails.knowledgeContent?.features
                val imageList = listOf(
                    R.drawable.cap,
                    R.drawable.cash_circle_logo,
                    R.drawable.coin_box_circle_logo
                )
                val featuresKey = featureMap?.keys?.toList()

                Spacer(modifier = Modifier.height(94.dp))

                LazyColumn {
                    item {
                        Text(
                            modifier = Modifier
                                .padding(start = 20.dp, bottom = 20.dp),
                            text = stopDailySavingBottomSheetDetails.header.orEmpty(),
                            fontFamily = jarFontFamily,
                            fontWeight = FontWeight.W400,
                            lineHeight = 24.sp,
                            color = Color.White,
                            fontSize = 16.sp
                        )

                        Text(
                            modifier = Modifier
                                .padding(start = 20.dp),
                            text = stopDailySavingBottomSheetDetails.knowledgeContent?.title.orEmpty(),
                            fontFamily = jarFontFamily,
                            fontWeight = FontWeight.W700,
                            color = Color.White,
                            lineHeight = 24.sp,
                            fontSize = 16.sp
                        )
                        Spacer(
                            Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                        )
                    }
                    featuresKey?.let {
                        items(featuresKey) {
                            it?.let { key ->
                                featureMap[key]?.let { it1 ->
                                    DailySavingBenefitBox(
                                        modifier = Modifier
                                            .padding(start = 16.dp, end = 16.dp),
                                        icon = imageList[key],
                                        text = generateSpannedFromHtmlString(
                                            it1,
                                            true
                                        ).toAnnotatedString()
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(28.dp))
                        AllUserLogo()
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        JarPrimaryButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp)
                                .padding(end = 16.dp),
                            color = colorResource(id = com.jar.app.core_ui.R.color.color_6038CE),
                            elevation = 0.dp,
                            text = stopDailySavingBottomSheetDetails.continueButtonText.orEmpty(),
                            isAllCaps = false,
                            borderBrush = null,
                            onClick = {
                                onContinueDailySavingClicked.invoke()
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        JarPrimaryButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp)
                                .padding(end = 16.dp),
                            elevation = 0.dp,
                            text = stopDailySavingBottomSheetDetails.stopButtonText.orEmpty(),
                            isAllCaps = false,
                            color = colorResource(id = com.jar.app.core_ui.R.color.color_3C3357),
                            borderBrush = null,
                            onClick = {
                                onStopDailySavingClicked.invoke()
                            }
                        )

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}
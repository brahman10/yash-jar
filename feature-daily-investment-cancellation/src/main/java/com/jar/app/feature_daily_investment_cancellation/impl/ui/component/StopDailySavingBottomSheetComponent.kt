package com.jar.app.feature_daily_investment_cancellation.impl.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_analytics.EventKey.is_Permanently_Cancel_flow
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.feature_daily_investment_cancellation.impl.ui.pause_daily_saving.CustomRadioButton
import com.jar.app.feature_daily_investment_cancellation.impl.ui.pause_daily_saving.PauseDailySavingBottomSheetViewModel
import com.jar.app.feature_daily_investment_cancellation.impl.ui.pause_daily_saving.convertDaysDifferenceToDate
import com.jar.app.feature_daily_investment_cancellation.impl.ui.pause_daily_saving.getKeyValue
import com.jar.app.feature_daily_investment_cancellation.impl.ui.pause_daily_saving.getPauseSavingData
import com.jar.app.feature_daily_investment_cancellation.impl.ui.stop_daily_saving.StopDailySavingBottomSheetViewModel
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEnum
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey.Button_type
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey.DSCancellation_StopDSpopup_Clicked
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentPauseKey
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentStatusScreen
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentPauseDetails
import com.jar.app.feature_daily_investment_tempering.R
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi

@Composable
fun StopDailySavingBottomSheetComponent() {
    Box(modifier = Modifier.fillMaxWidth())
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer(
                    shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp), clip = true
                )
                .background(colorResource(id = com.jar.app.core_ui.R.color.color_2E2942))
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.Start)
                    .fillMaxWidth(),
                painter = painterResource(id = com.jar.app.core_ui.R.drawable.lamp_logo),
                contentDescription = ""
            )
        }

        Box(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .align(Alignment.BottomCenter)
        ) {
            Column {

                JarPrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp)
                        .padding(end = 16.dp),
                    text = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.continue_daily_savings),
                    isAllCaps = false,
                    onClick = {

                    }
                )
                Spacer(modifier = Modifier.height(12.dp))

                JarPrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp)
                        .padding(end = 16.dp),
                    text = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.stop_daily_savings),
                    isAllCaps = false,
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_3C3357),
                    onClick = {

                    }
                )

                Spacer(modifier = Modifier.height(37.dp))
            }
        }
    }
}

@Composable
fun DailySavingBenefitBox(
    modifier: Modifier,
    icon: Int,
    text: AnnotatedString,
    color: Color = colorResource(id = com.jar.app.core_ui.R.color.color_433D59),
    textColor: Color = colorResource(id = com.jar.app.core_ui.R.color.color_EBB46A),
    textSize: TextUnit = 14.sp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = color,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(painter = painterResource(id = icon), contentDescription = "")

            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontFamily = jarFontFamily,
                fontWeight = FontWeight.W400,
                color = textColor,
                fontSize = textSize
            )
        }
    }
}

@Preview
@Composable
fun RatingCardTest() {
    RatingCard(
        stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.rating_4_4),
        stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.one_core_downloads)
    )
}

@Composable
fun DsStatus(
    text: String,
    imageResource: Int,
    backgroundColor: Color,
    ) {
    Row {
        Image(
            painter = painterResource(id = imageResource), contentDescription = "",
            modifier = Modifier
                .size(16.dp)
                .align(Alignment.CenterVertically)
        )
        
        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = text,
            modifier = Modifier,
            color = backgroundColor,
            style = JarTypography.body1.copy(
                fontWeight = FontWeight.W700,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
fun RatingCard(rating: String, downloads: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.small_star),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 5.dp, end = 6.dp)
        )
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .background(
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_3C3357),
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.big_star),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 9.dp)
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = rating,
                        fontFamily = jarFontFamily,
                        fontWeight = FontWeight.W700,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Image(
                        painter = painterResource(R.drawable.glow_star),
                        contentDescription = "",
                    )
                    Image(
                        painter = painterResource(R.drawable.glow_star),
                        contentDescription = "",
                    )
                    Image(
                        painter = painterResource(R.drawable.glow_star),
                        contentDescription = "",
                    )
                    Image(
                        painter = painterResource(R.drawable.glow_star),
                        contentDescription = "",
                    )
                    Image(
                        painter = painterResource(R.drawable.half_star),
                        contentDescription = "",
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = downloads,
                    fontFamily = jarFontFamily,
                    fontWeight = FontWeight.W400,
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2),
                    fontSize = 12.sp
                )
            }
        }
    }
}


@Composable
internal fun StopDailySavingBottomSheet(
    pauseDetails: DailyInvestmentPauseDetails,
    onDismissClick: () -> Unit,
    pauseViewModel: PauseDailySavingBottomSheetViewModel,
    stopDailySavingViewModel: StopDailySavingBottomSheetViewModel,
    analyticsHandler: AnalyticsApi
    ) {

    val estimationOfDate = pauseViewModel.estimatedDaysFlow.collectAsState(initial = null)
    var selectedRadioButton by remember { mutableStateOf(0) }
    val selectedColor = colorResource(id = com.jar.app.core_ui.R.color.color_3C3357)
    val unselectedColor = colorResource(id = com.jar.app.core_ui.R.color.color_272239)
    val selectDateString =
        stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.select_the_date)
    val selectedDataFlow = pauseViewModel.selectedDaysFlow.collectAsState(initial = null)
    val chosenCustomDate = pauseViewModel.customDaysFlow.collectAsState(initial = null)

    Box {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .debounceClickable {
                        onDismissClick.invoke()
                        analyticsHandler.postEvent(
                            DSCancellation_StopDSpopup_Clicked, mapOf(
                                Button_type to "Close",
                                is_Permanently_Cancel_flow to true
                            )
                        )
                    }
                    .padding(top = 24.dp, end = 22.dp)
                    .fillMaxWidth()
                    .align(Alignment.End),
                painter = painterResource(id = R.drawable.cross_without_boarder),
                alignment = Alignment.TopEnd,
                contentDescription = ""
            )
        }
    }

    Row(
        modifier = Modifier
            .background(
                color = colorResource(id = com.jar.app.core_ui.R.color.color_272239),
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )
            .padding(top = 48.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        LazyColumn {
            val map = pauseDetails.pauseDaysMap.orEmpty()

            item {
                Text(
                    text = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.stop_your_daily_savings),
                    color = Color.White,
                    modifier = Modifier.padding(start = 16.dp),
                    style = JarTypography.h4.copy(lineHeight = 26.sp)
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            items(4) { index ->
                Box(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp)
                        .fillMaxWidth()
                        .border(
                            color = colorResource(id = com.jar.app.core_ui.R.color.color_3C3357),
                            width = 1.dp,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .background(
                            color = if (selectedRadioButton == index) {
                                selectedColor
                            } else {
                                unselectedColor
                            },
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .debounceClickable {
                                selectedRadioButton = index
                                map.let {
                                    if (selectedRadioButton == 3) {
                                        pauseViewModel.setTotalDay(
                                            0,
                                            DailyInvestmentCancellationEnum.V3.name
                                        )
                                        analyticsHandler.postEvent(
                                            DSCancellation_StopDSpopup_Clicked,
                                            mapOf(
                                                DailyInvestmentCancellationEventKey.Stop_Type to DailyInvestmentStatusScreen.Permanent,
                                                is_Permanently_Cancel_flow to true
                                            )
                                        )
                                    } else {
                                        getKeyValue(index, map)?.let { it1 ->
                                            pauseViewModel.setTotalDay(
                                                it1,
                                                DailyInvestmentCancellationEnum.V3.name
                                            )
                                            pauseViewModel.setDifferenceDays(it1)
                                            pauseViewModel.setEstimatedDate(
                                                convertDaysDifferenceToDate(it1)
                                            )

                                            analyticsHandler.postEvent(
                                                DSCancellation_StopDSpopup_Clicked,
                                                mapOf(
                                                    DailyInvestmentCancellationEventKey.Stop_Type to DailyInvestmentStatusScreen.Day,
                                                    DailyInvestmentPauseKey.No_of_days to it1.toString(),
                                                    is_Permanently_Cancel_flow to true
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (selectedRadioButton == 0) {
                            map.let {
                                getKeyValue(0, map)?.let { it1 ->
                                    pauseViewModel.setTotalDay(it1, DailyInvestmentCancellationEnum.V3.name)
                                    pauseViewModel.setDifferenceDays(it1)
                                    pauseViewModel.setEstimatedDate(
                                        convertDaysDifferenceToDate(it1)
                                    )
                                }
                            }
                        }
                        CustomRadioButton(
                            selected = (selectedRadioButton == index),
                            onClick = {
                                selectedRadioButton = index
                            },
                            modifier = Modifier
                                .padding(top = 13.dp, bottom = 13.dp, start = 14.dp)
                                .align(Alignment.CenterVertically)
                                .size(24.dp)
                                .selectable(
                                    selected = (selectedRadioButton == index),
                                    onClick = {
                                        selectedRadioButton = index
                                        map.let {
                                            if (selectedRadioButton == 3) {
                                                pauseViewModel.setTotalDay(
                                                    0,
                                                    DailyInvestmentCancellationEnum.V3.name
                                                )
                                                analyticsHandler.postEvent(
                                                    DSCancellation_StopDSpopup_Clicked,
                                                    mapOf(
                                                        DailyInvestmentCancellationEventKey.Stop_Type to DailyInvestmentStatusScreen.Permanent,
                                                        is_Permanently_Cancel_flow to true
                                                    )
                                                )
                                            } else {
                                                getKeyValue(index, map)?.let { it1 ->
                                                    pauseViewModel.setTotalDay(
                                                        it1,
                                                        DailyInvestmentCancellationEnum.V3.name
                                                    )
                                                    pauseViewModel.setDifferenceDays(it1)
                                                    pauseViewModel.setEstimatedDate(
                                                        convertDaysDifferenceToDate(it1)
                                                    )

                                                    analyticsHandler.postEvent(
                                                        DSCancellation_StopDSpopup_Clicked,
                                                        mapOf(
                                                            DailyInvestmentCancellationEventKey.Stop_Type to DailyInvestmentStatusScreen.Day,
                                                            DailyInvestmentPauseKey.No_of_days to it1.toString(),
                                                            is_Permanently_Cancel_flow to true
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                )
                        )

                        Text(
                            text = "${getKeyValue(index, map)?.let { getTotalTime(it) }}",
                            fontFamily = jarFontFamily,
                            color = Color.White,
                            style = JarTypography.bodyRegular.copy(
                                fontWeight = FontWeight.W700
                            ),
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .weight(1f)
                        )

                        Text(
                            text = getKeyValue(index, map)?.let { map.getValue(it) }.orEmpty(),
                            style = JarTypography.bodyRegular.copy(
                                fontWeight = FontWeight.W400
                            ),
                            fontFamily = jarFontFamily,
                            color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
                            modifier = Modifier.padding(end = 14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = JarTypography.caption.copy(
                    ),
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_EBB46A),
                    text = if (selectedRadioButton == 3) {
                        ""
                    } else {
                        stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.your_automatically_resume_on) + " ${estimationOfDate.value}"
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(11.dp))
                JarPrimaryButton(
                    text = "Stop Now",
                    onClick = {
                        if (pauseViewModel.selectedDaysFlow.value == 0) {
                            stopDailySavingViewModel.disableDailySavings(version = DailyInvestmentCancellationEnum.V3.name)
                        } else {
                            val pauseDailySavingData = selectedDataFlow.value?.let {
                                getPauseSavingData(
                                    it
                                )
                            }
                            pauseViewModel.updateAutoInvestPauseDurationFlow(
                                pause = true,
                                pauseDailySavingData = pauseDailySavingData,
                                customDuration = null,
                                version = DailyInvestmentCancellationEnum.V3.name
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    isAllCaps = false
                )
                Spacer(modifier = Modifier.height(39.dp))
            }
        }
    }
}

@Composable
fun getTotalTime(days: Int): String {
    return when (days) {
        1 -> {
            stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.till_tomorrow_c)
        }

        7 -> {
            stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.for_one_week_c)
        }

        14 -> {
            stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.for_two_week_c)
        }

        else -> {
            stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.permanently)
        }
    }
}


@Composable
fun AllUserLogo() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.group_user_logo),
            contentDescription = "",
            Modifier.height(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.one_core_people_save_on_jar),
            fontFamily = jarFontFamily,
            fontWeight = FontWeight.W400,
            color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2),
            fontSize = 12.sp
        )
    }
}

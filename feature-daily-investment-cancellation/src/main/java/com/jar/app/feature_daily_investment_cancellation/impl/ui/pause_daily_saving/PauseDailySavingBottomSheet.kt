package com.jar.app.feature_daily_investment_cancellation.impl.ui.pause_daily_saving

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.jar.app.base.util.epochToDate
import com.jar.app.base.util.getFormattedDate
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.feature_daily_investment.shared.domain.model.PauseDailySavingData
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey.Button_type
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey.DSCancellation_PauseDSpopupClicked
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentPauseKey.No_of_days
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentPauseKey.Pause_Type
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentStatusScreen
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentPauseDetails
import com.jar.app.feature_daily_investment_tempering.R
import com.jar.app.feature_user_api.domain.model.PauseSavingOption
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit


@Composable
internal fun RenderPauseDailySavingStaticBottomSheet(
    pauseDetails: DailyInvestmentPauseDetails,
    viewModel: PauseDailySavingBottomSheetViewModel,
    onDismissClick: () -> Unit,
    childFragmentManager: FragmentManager,
    requireContext: Context,
    analyticsHandler: AnalyticsApi
) {
    val estimationOfDate = viewModel.estimatedDaysFlow.collectAsState(initial = null)
    val chosenCustomDate = viewModel.customDaysFlow.collectAsState(initial = null)
    var selectedRadioButton by remember { mutableStateOf(0) }
    val options = listOf("2", "8", "12")


    val backGroundColor = remember { mutableStateOf(Color(0xFF272239)) }
    val estimatedResumeDate = remember { mutableStateOf("") }
    val selectedColor = colorResource(id = com.jar.app.core_ui.R.color.color_3C3357)
    val unselectedColor = colorResource(id = com.jar.app.core_ui.R.color.color_272239)
    val selectDateString =
        stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.select_the_date)
    val totalDaysMutableValue =
        remember { mutableStateOf(selectDateString) }
    val totalDaysTextColor = remember { mutableStateOf(Color.White) }
    var selectedDate by remember { mutableStateOf("") }


    val selectedDataFlow = viewModel.selectedDaysFlow.collectAsState(initial = null)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                colorResource(id = com.jar.app.core_ui.R.color.color_272239),
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)

            ),
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .align(Alignment.BottomCenter)
        ) {
            Column {
                Image(
                    painter = painterResource(R.drawable.cross_without_boarder),
                    contentDescription = "",
                    modifier = Modifier
                        .debounceClickable {
                            analyticsHandler.postEvent(
                                DSCancellation_PauseDSpopupClicked,
                                mapOf(Button_type to DailyInvestmentStatusScreen.Close)
                            )
                            onDismissClick.invoke()
                        }
                        .align(Alignment.End)
                        .padding(top = 23.dp, end = 23.dp)
                )
                Text(
                    modifier = Modifier.padding(top = 39.dp, start = 16.dp),
                    text = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.pause_your_daily_savings_for),
                    fontFamily = jarFontFamily,
                    fontWeight = FontWeight.W700,
                    lineHeight = 18.sp,
                    color = Color.White,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    val map = pauseDetails.pauseDaysMap.orEmpty()

                    items(options.size) { index ->
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
                                        map?.let {
                                            getKeyValue(index, map)?.let { it1 ->
                                                viewModel.setTotalDay(it1)
                                                viewModel.setDifferenceDays(it1)
                                                viewModel.setEstimatedDate(
                                                    convertDaysDifferenceToDate(it1)
                                                )
                                                analyticsHandler.postEvent(
                                                    DSCancellation_PauseDSpopupClicked,
                                                    mapOf(
                                                        Pause_Type to DailyInvestmentStatusScreen.Day,
                                                        No_of_days to it1.toString()
                                                    )
                                                )
                                            }
                                        }
                                    }
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (selectedRadioButton == 0) {
                                    map?.let {
                                        getKeyValue(0, map)?.let { it1 ->
                                            viewModel.setTotalDay(it1)
                                            viewModel.setDifferenceDays(it1)
                                            viewModel.setEstimatedDate(
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
                                                map?.let {
                                                    getKeyValue(index, map)?.let { it1 ->
                                                        viewModel.setTotalDay(it1)
                                                        viewModel.setDifferenceDays(it1)
                                                        viewModel.setEstimatedDate(
                                                            convertDaysDifferenceToDate(it1)
                                                        )
                                                        analyticsHandler.postEvent(
                                                            DSCancellation_PauseDSpopupClicked,
                                                            mapOf(
                                                                Pause_Type to DailyInvestmentStatusScreen.Day,
                                                                No_of_days to it1.toString()
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        )
                                )

                                Text(
                                    text = "${getKeyValue(index, map)} Days",
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
                                    text = "${
                                        getKeyValue(index, map)
                                            ?.let { map.getValue(it) }
                                    }",
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
                        Spacer(modifier = Modifier.height(12.dp))

                        OrRow()

                        Spacer(modifier = Modifier.height(24.dp))

                        Box(
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp)
                                .fillMaxWidth()
                                .border(
                                    color = colorResource(id = com.jar.app.core_ui.R.color.color_3C3357),
                                    width = 1.dp,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .background(
                                    color = if (selectedRadioButton == 4) {
                                        selectedColor
                                    } else {
                                        unselectedColor
                                    }, shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            val currentDate = remember {
                                Calendar.getInstance()
                            }
                            var dob = ""
                            val constraintsBuilder = CalendarConstraints.Builder()
                            constraintsBuilder.setValidator(
                                DateValidatorPointForward.from(
                                    currentDate.timeInMillis
                                )
                            )
                            constraintsBuilder.setStart(
                                currentDate.timeInMillis + TimeUnit.DAYS.toMillis(
                                    1
                                )
                            )
                            constraintsBuilder.setEnd(
                                currentDate.timeInMillis + TimeUnit.DAYS.toMillis(
                                    6 * 30
                                )
                            )

                            val materialDatePicker =
                                MaterialDatePicker.Builder.datePicker()
                                    .setTheme(com.jar.app.core_ui.R.style.ThemeOverlay_App_ComposeDatePicker)
                                    .setCalendarConstraints(constraintsBuilder.build())
                                    .build()

                            materialDatePicker.addOnPositiveButtonClickListener {
                                dob = it.epochToDate().getFormattedDate("dd MMM''yy")
                                selectedDate = dob
                                viewModel.setCustomDate(it)

                                val pickedDate: Calendar = Calendar.getInstance().apply {
                                    timeInMillis = it
                                }
                                val daysDifference = TimeUnit.DAYS.convert(
                                    pickedDate.timeInMillis - currentDate.timeInMillis,
                                    TimeUnit.MILLISECONDS
                                ).toInt()

                                totalDaysMutableValue.value = "${daysDifference + 1} Days"
                                estimatedResumeDate.value =
                                    convertDaysDifferenceToDate(daysDifference)
                                viewModel.setEstimatedDate(
                                    convertDaysDifferenceToDate(
                                        daysDifference + 1
                                    )
                                )
                                viewModel.setDifferenceDays(daysDifference + 1)
                                if (totalDaysMutableValue.value != requireContext.resources.getString(
                                        com.jar.app.feature_daily_investment_cancellation.shared.R.string.select_the_date
                                    )
                                ) {
                                    backGroundColor.value = Color(0xFF3C3357)
                                    totalDaysTextColor.value = Color(0xFFACA1D3)
                                } else {
                                    backGroundColor.value = Color(0xFF272239)
                                    totalDaysTextColor.value = Color(0xFF776E94)
                                }
                            }


                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .debounceClickable {
                                        selectedRadioButton = 4
                                        analyticsHandler.postEvent(
                                            DSCancellation_PauseDSpopupClicked,
                                            mapOf(
                                                Pause_Type to DailyInvestmentStatusScreen.Date
                                            )
                                        )
                                        materialDatePicker.show(
                                            childFragmentManager,
                                            null
                                        )
                                    }
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.calender),
                                    contentDescription = ""
                                )

                                Text(
                                    modifier = Modifier
                                        .padding(start = 14.dp)
                                        .weight(1f),
                                    text = totalDaysMutableValue.value,
                                    fontFamily = jarFontFamily,
                                    color = if (totalDaysMutableValue.value ==
                                        requireContext.resources.getString(
                                            com.jar.app.feature_daily_investment_cancellation.shared.R.string.select_the_date
                                        )) colorResource(id = com.jar.app.core_ui.R.color.color_776E94) else Color.White,
                                    style = JarTypography.bodyRegular.copy(
                                        fontWeight = if (totalDaysMutableValue.value == requireContext.resources.getString(
                                                com.jar.app.feature_daily_investment_cancellation.shared.R.string.select_the_date
                                            )) FontWeight.W400 else FontWeight.W700
                                    )
                                )

                                selectedDate.takeIf { it.isNotBlank() }?.let {
                                    Text(
                                        modifier = Modifier.padding(start = 14.dp),
                                        text = "${stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.till)} $selectedDate",
                                        fontFamily = jarFontFamily,
                                        color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
                                        style = JarTypography.bodyRegular.copy(
                                            fontWeight = FontWeight.W700
                                        )
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                    }

                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier.padding(start = 16.dp),
                                text = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.your_automatically_resume_on) + " ${estimationOfDate.value}",
                                textAlign = TextAlign.Center,
                                fontFamily = jarFontFamily,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.W400,
                                style = JarTypography.caption.copy(
                                    fontWeight = FontWeight.W400
                                ),
                                color = colorResource(id = com.jar.app.core_ui.R.color.color_EBB46A)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            JarPrimaryButton(modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp)
                                .padding(end = 16.dp),
                                elevation = 0.dp,
                                text = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.pause_now),
                                isAllCaps = false,
                                onClick = {
                                    val pauseDailySavingData = selectedDataFlow.value?.let {
                                        getPauseSavingData(
                                            it
                                        )
                                    }
                                    viewModel.updateAutoInvestPauseDurationFlow(
                                        pause = true,
                                        pauseDailySavingData = pauseDailySavingData,
                                        customDuration = chosenCustomDate.value
                                    )
                                })
                            Spacer(modifier = Modifier.height(39.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.left_faded_line),
            contentDescription = ""
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "OR", fontFamily = jarFontFamily,
            style = JarTypography.bodyRegular.copy(
                fontWeight = FontWeight.W400
            ),
            color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
        )

        Spacer(modifier = Modifier.width(12.dp))

        Image(
            painter = painterResource(id = R.drawable.right_faded_line),
            contentDescription = ""
        )
    }
}

@Composable
fun CustomRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier
) {
    modifier
        .size(20.dp)
        .debounceClickable { onClick?.invoke() }
    if (selected) {
        Image(
            painter = painterResource(id = R.drawable.radio_button_selected),
            contentDescription = "",
            modifier = modifier
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.radio_button_unselected),
            contentDescription = "",
            modifier = modifier
        )
    }
}

internal fun getKeyValue(index: Int, map: Map<Int, String>): Int? {
    return map.keys.elementAtOrNull(index)
}

internal fun convertDaysDifferenceToDate(numberOfDays: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, numberOfDays)

    val date = calendar.time
    return SimpleDateFormat("dd MMM''yy", Locale.getDefault()).format(date).toString()
}

internal fun getPauseSavingData(numberOfDays: Int): PauseDailySavingData {
    return when (numberOfDays) {
        1 -> PauseDailySavingData(PauseSavingOption.ONE)
        2 -> PauseDailySavingData(PauseSavingOption.TWO)
        7 -> PauseDailySavingData(PauseSavingOption.WEEK)
        8 -> PauseDailySavingData(PauseSavingOption.EIGHT)
        14 -> PauseDailySavingData(PauseSavingOption.TWO_WEEKS)
        else -> {
            PauseDailySavingData(PauseSavingOption.TWELVE)
        }
    }
}
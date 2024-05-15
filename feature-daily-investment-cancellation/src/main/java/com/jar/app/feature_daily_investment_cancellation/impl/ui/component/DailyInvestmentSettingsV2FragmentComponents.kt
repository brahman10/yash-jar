package com.jar.app.feature_daily_investment_cancellation.impl.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.component.JarButton
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.JarSecondaryButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarColors
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.core_compose_ui.utils.generateSpannedFromHtmlString
import com.jar.app.core_compose_ui.utils.toAnnotatedString
import com.jar.app.core_compose_ui.views.TransparentPillButtonWithImage
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEnum
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey.Button_type
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey.DSCancellation_PageClicked
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey.Gold_Delivery_Icon
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey.Stop_Icon
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey.Withdraw_Icon
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentSettingsData
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.SetupDetails
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.StepsFeaturesDetails
import com.jar.app.feature_daily_investment_tempering.R
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.SavingsDetails
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi

@Composable
private fun NextDailySavingDeductionDetails(
    modifier: Modifier = Modifier, nextDeductionDetails: String
) {
    Row(
        modifier = modifier
            .background(JarColors.primaryButtonBgColorV2)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = modifier
                .padding(top = 8.dp)
                .padding(bottom = 8.dp),
            text = nextDeductionDetails,
            color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2),
            fontWeight = FontWeight.W400,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            fontFamily = jarFontFamily
        )
    }
}

@Composable
private fun DailyInvestmentAmountDetails(
    modifier: Modifier = Modifier,
    savingsDetails: SavingsDetails
) {
    Column(
        modifier = modifier
            .background(JarColors.spendTrackerPrimaryBackground)
            .padding(start = 10.dp)
            .padding(end = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier,
            text = savingsDetails.savedText.orEmpty(),
            fontSize = 14.sp,
            fontWeight = FontWeight.W400,
            lineHeight = 20.sp,
            fontFamily = jarFontFamily,
            color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3)
        )
        Text(
            modifier = Modifier
                .padding(top = 5.dp)
                .padding(bottom = 5.dp),
            text = "₹${savingsDetails.totalDsAmount}",
            fontSize = 40.sp,
            fontFamily = jarFontFamily,
            fontWeight = FontWeight.W700,
            color = Color.White
        )
        Text(
            modifier = Modifier,
            text = generateSpannedFromHtmlString(
                savingsDetails.savingInText,
                true
            ).toAnnotatedString(),
            fontSize = 16.sp,
            fontWeight = FontWeight.W400,
            fontFamily = jarFontFamily,
            color = colorResource(id = com.jar.app.core_ui.R.color.color_EBB46A)
        )
    }
}

@Composable
fun TotalAmountSavedDetailsBlock(
    modifier: Modifier,
    onTrackClick: () -> Unit,
    dailySavingPause: Boolean,
    savingsDetails: SavingsDetails
) {
    val coinHeight = remember {
        mutableStateOf(45.dp)
    }
    Box {
        Column(
            modifier = modifier
                .background(JarColors.spendTrackerPrimaryBackground)
                .fillMaxWidth()
                .padding(top = 24.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            savingsDetails.savingInText?.let {
                DailyInvestmentAmountDetails(
                    savingsDetails = savingsDetails,
                    modifier = modifier,
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            JarPrimaryButton(
                modifier = Modifier
                    .height(42.dp)
                    .width(214.dp),
                text = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.track_your_saving),
                color = Color(0xFF3C3357),
                icon = R.drawable.calender,
                fontWeight = FontWeight.W600,
                isAllCaps = false,
                elevation = 0.dp,
                onClick = {
                    onTrackClick()
                },
                borderBrush = null
            )
            Spacer(modifier = Modifier.height(28.dp))
            if (dailySavingPause) {
                coinHeight.value = 33.dp
                Box(modifier = Modifier.size(0.dp))
            } else {
                coinHeight.value = 45.dp
                NextDailySavingDeductionDetails(nextDeductionDetails = savingsDetails.autoDebitDateText.orEmpty())
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .background(colorResource(id = com.jar.app.core_ui.R.color.color_272239))
            )
        }

        Image(
            painter = painterResource(id = R.drawable.left_top_coin),
            contentDescription = "",
            modifier = Modifier.align(Alignment.TopStart)
        )
        Image(
            painter = painterResource(id = R.drawable.right_top_coin),
            contentDescription = "",
            modifier = Modifier.align(Alignment.TopEnd)
        )
        Image(
            painter = painterResource(id = R.drawable.left_bottom_coin),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = coinHeight.value)
        )
        Image(
            painter = painterResource(id = R.drawable.right_bottom_coin),
            contentDescription = "",
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

@Composable
fun DailyInvestmentStepsFeaturesDetailsBlock(
    modifier: Modifier, stepsFeaturesDetails: StepsFeaturesDetails?
) {
    Column(
        modifier = modifier
            .background(
                colorResource(id = com.jar.app.core_ui.R.color.color_272239)
            )
            .padding(start = 14.dp)
            .padding(end = 14.dp)
    ) {

        DailySavingFeaturesBox(modifier = modifier, stepsFeaturesDetails)

        Spacer(modifier = Modifier.heightIn(28.dp))
    }

}

@Preview
@Composable
fun TransparentPillButtonPreview() {
    TransparentPillButtonWithImage(
        imageResource = R.drawable.active_status_icon,
        text = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.active_status_pill),
        backgroundColor = Color(0xFF58DDC8),
        cornerRadius = 3.dp
    ) {}
}

@Composable
fun DailyInvestmentSetupDetailsBlock(
    modifier: Modifier,
    onStopClick: () -> Unit,
    onPauseClick: () -> Unit,
    onAmountClick: () -> Unit,
    onSavingSourceClick: () -> Unit,
    dailySavingAmount: State<Float?>,
    dailySavingPause: Boolean = false,
    setupDetails: SetupDetails?,
    totalUpiApps: Int?,
    setUpVersion: String,
    analyticsHandler: AnalyticsApi
) {

    val getPaymentSource = remember {
        setupDetails?.subProvider?.let {
            getMandateSourceName(it)
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {

        Column(
            modifier = modifier
                .fillMaxWidth()
                .graphicsLayer(
                    shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp), clip = true
                )
                .background(colorResource(id = com.jar.app.core_ui.R.color.color_2E2942))
        ) {
            Image(
                painter = painterResource(R.drawable.rectangle_3),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = setupDetails?.header.orEmpty(),
                modifier = Modifier
                    .padding(top = 40.dp)
                    .padding(start = 16.dp)
                    .padding(bottom = 19.dp),
                fontFamily = jarFontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
                color = colorResource(id = com.jar.app.core_ui.R.color.color_EEEAFF)
            )

            SetupDetailOneRow(
                onEditClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
                source = "",
                text = setupDetails?.detailsOrderText?.get(0).orEmpty(),
                lineShow = true,
                isDailySavingPaused = dailySavingPause,
                showStatusPill = true,
                setUpVersion = setUpVersion
            )

            SetupDetailOneRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
                source = "₹" + dailySavingAmount.value?.toInt().orZero(),
                onEditClick = {
                    if (!dailySavingPause) {
                        onAmountClick()
                    }
                },
                text = setupDetails?.detailsOrderText?.get(1).orEmpty(),
                lineShow = true,
                painter = if (dailySavingPause) {
                    painterResource(id = R.drawable.edit_logo_v2)
                } else {
                    painterResource(R.drawable.edit_logo)
                },
                setUpVersion = setUpVersion
            )

            getPaymentSource?.let {
                SetupDetailOneRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    source = it,
                    onEditClick = {
                        if (totalUpiApps.orZero() > 1 && !dailySavingPause) {
                            onSavingSourceClick()
                        }
                    },
                    text = setupDetails?.detailsOrderText?.get(2).orEmpty(),
                    lineShow = false,
                    painter = if (totalUpiApps.orZero() > 1 && !dailySavingPause) {
                        painterResource(R.drawable.edit_logo)
                    } else {
                        painterResource(R.drawable.edit_logo_v2)
                    },
                    setUpVersion = setUpVersion
                )
            }

            Spacer(modifier = Modifier.height(35.dp))

            if (setupDetails?.status == DailyInvestmentCancellationEnum.ACTIVE.name) {
                if (setUpVersion == DailyInvestmentCancellationEnum.V3.name || setUpVersion == DailyInvestmentCancellationEnum.V4.name) {
                    ButtonLayoutForV2AndV3(
                        firstButtonText = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.edit_daily_saving_amount),
                        secondButtonText = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.stop_daily_savings),
                        dsStatus = setupDetails.status,
                        onTopButtonClick = { onAmountClick() },
                        onBottomButtonClick = { onStopClick() },
                        analyticsHandler = analyticsHandler
                    )
                } else {
                    ButtonLayoutForV1(
                        onStopClick = { onStopClick() },
                        onPauseClick = { onPauseClick() },
                        dailySavingPause = dailySavingPause
                    )
                }
            } else {
                when (setUpVersion) {
                    DailyInvestmentCancellationEnum.V3.name -> {
                        JarPrimaryButton(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth(),
                            icon = R.drawable.play_icon,
                            isAllCaps = false,
                            iconSize = DpSize(20.dp, 20.dp),
                            text = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.restart_daily_savings),
                            onClick = {
                                onPauseClick()
                            }
                        )
                    }
                    DailyInvestmentCancellationEnum.V4.name -> {
                        ButtonLayoutForV2AndV3(
                            firstButtonText = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.resume_daily_savings),
                            secondButtonText = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.stop_daily_savings),
                            dsStatus = setupDetails?.status,
                            onTopButtonClick = { onPauseClick() },
                            onBottomButtonClick = { onStopClick() },
                            analyticsHandler = analyticsHandler
                        )
                    }
                    else -> {
                        ButtonLayoutForV1(
                            onStopClick = { onStopClick() },
                            onPauseClick = { onPauseClick() },
                            dailySavingPause = dailySavingPause
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Image(
                painter = painterResource(id = R.drawable.trust_frame),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 73.dp)
                    .padding(end = 73.dp)
                    .height(18.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(40.dp))
        }

    }
}

@Composable
fun ButtonLayoutForV1(
    onStopClick: () -> Unit,
    onPauseClick: () -> Unit,
    dailySavingPause: Boolean
) {
    Row {
        JarPrimaryButton(modifier = Modifier
            .height(56.dp)
            .width(156.dp)
            .fillMaxWidth()
            .weight(1f)
            .padding(start = 16.dp),

            fontWeight = FontWeight.W600,
            isAllCaps = false,
            text = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.stop),
            iconSize = DpSize(20.dp, 20.dp),
            textColor = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2),
            elevation = 0.dp,
            color = colorResource(id = com.jar.app.core_ui.R.color.color_272239),
            icon = R.drawable.left_icon,
            borderBrush = null,
            onClick = {
                onStopClick()
            })

        Spacer(modifier = Modifier.width(16.dp))

        JarPrimaryButton(modifier = Modifier
            .height(56.dp)
            .width(156.dp)
            .fillMaxWidth()
            .weight(1f)
            .padding(end = 16.dp),
            text = if (dailySavingPause)
                stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.resume)
            else
                stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.pause),
            elevation = 0.dp,
            iconSize = DpSize(20.dp, 20.dp),
            icon = if (dailySavingPause) R.drawable.play_icon else R.drawable.right_icon,
            fontWeight = FontWeight.W600,
            isAllCaps = false,
            borderBrush = null,
            color = if (dailySavingPause)
                colorResource(id = com.jar.app.core_ui.R.color.color_6038CE)
            else colorResource(
                id = com.jar.app.core_ui.R.color.color_3C3357
            ),
            onClick = {
                onPauseClick()
            })
    }

}

@Composable
fun ButtonLayoutForV2AndV3(
    firstButtonText: String,
    secondButtonText: String,
    onTopButtonClick: () -> Unit,
    onBottomButtonClick: () -> Unit,
    dsStatus: String? = null,
    analyticsHandler: AnalyticsApi
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        JarPrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            icon = if (dsStatus == DailyInvestmentCancellationEnum.ACTIVE.name) { R.drawable.edit_logo } else { R.drawable.play_icon },
            isAllCaps = false,
            iconSize = DpSize(20.dp, 20.dp),
            text = firstButtonText,

            onClick = {
                onTopButtonClick()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        JarSecondaryButton(
            modifier = Modifier.fillMaxWidth(),
            icon = R.drawable.left_icon,
            isAllCaps = false,
            iconSize = DpSize(20.dp, 20.dp),
            color = colorResource(id = com.jar.app.core_ui.R.color.color_272239),
            borderColor = colorResource(id = com.jar.app.core_ui.R.color.color_3C3357),
            text = secondButtonText,
            textColor = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2),
            onClick = {
                onBottomButtonClick()
            }
        )
    }
}

private fun getMandateSourceName(source: String): String {
    return source.replace("_", "").toLowerCase().capitalize()
}

@Composable
fun CircleShapeLogo(color: Color, text: String) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(color = color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = jarFontFamily,
            fontWeight = FontWeight.W700,
            color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3)
        )
    }
}

@Composable
private fun HowDailySavingWorksTextInfo(
    modifier: Modifier, stepsFeaturesDetails: StepsFeaturesDetails
) {
    Column(
        modifier = modifier
            .background(colorResource(id = com.jar.app.core_ui.R.color.color_272239))
            .padding(start = 16.dp)
            .padding(end = 10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Column {
                    CircleShapeLogo(
                        colorResource(id = com.jar.app.core_ui.R.color.color_3C3357),
                        "1"
                    )
                    Canvas(
                        modifier = Modifier.padding(start = 16.dp),
                        onDraw = {
                            drawLine(
                                color = Color(0xFF3C3357),
                                start = Offset.Zero,
                                end = Offset(0f, 100f),
                                strokeWidth = 1f,
                            )
                        },
                    )
                }
                Text(
                    text = generateSpannedFromHtmlString(
                        stepsFeaturesDetails.stepsOrderText?.get(
                            0
                        ) ?: "", true
                    ).toAnnotatedString(),
                    fontFamily = jarFontFamily,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.W400,
                    modifier = modifier.padding(start = 10.dp),
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
            Canvas(
                modifier = Modifier.padding(start = 16.dp),
                onDraw = {
                    drawLine(
                        color = Color(0xFF3C3357),
                        start = Offset.Zero,
                        end = Offset(0f, 100f),
                        strokeWidth = 1f,
                    )
                },
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = modifier
            ) {
                CircleShapeLogo(colorResource(id = com.jar.app.core_ui.R.color.color_3C3357), "2")
                Text(
                    text = generateSpannedFromHtmlString(
                        stepsFeaturesDetails.stepsOrderText?.get(
                            1
                        ) ?: "", true
                    ).toAnnotatedString(),
                    fontFamily = jarFontFamily,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.W400,
                    modifier = modifier.padding(start = 10.dp),
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

        }
    }
}

@Composable
fun HowDailySavingWorksDetails(stepsFeaturesDetails: StepsFeaturesDetails) {
    Row(
        modifier = Modifier
            .background(colorResource(id = com.jar.app.core_ui.R.color.color_272239))
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HowDailySavingWorksTextInfo(
            modifier = Modifier.align(Alignment.Top), stepsFeaturesDetails = stepsFeaturesDetails
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 10.dp, end = 10.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Image(
                painter = painterResource(id = R.drawable.intro_screen_locker),
                contentDescription = "",
                modifier = Modifier
                    .size(width = 122.dp, height = 155.dp)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
fun SecondPartOfSpendTrackerScreenInfo(
    stepsFeaturesDetails: StepsFeaturesDetails
) {
    Column(
        modifier = Modifier.background(colorResource(id = com.jar.app.core_ui.R.color.color_272239))
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = stepsFeaturesDetails.header.orEmpty(),
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontFamily = jarFontFamily,
            fontWeight = FontWeight.W700,
            color = Color.White
        )
        HowDailySavingWorksDetails(stepsFeaturesDetails = stepsFeaturesDetails)
    }
}

@Composable
private fun SpendTrackerScreenBoxLogo(
    modifier: Modifier, imageResource: Int, body: String
) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = imageResource), contentDescription = "")
        Spacer(modifier = Modifier.heightIn(8.dp))
        Text(
            text = generateSpannedFromHtmlString(body, true).toAnnotatedString(),
            fontFamily = jarFontFamily,
            fontSize = 10.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.W400,
            textAlign = TextAlign.Center,
            color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2)
        )
    }
}

@Composable
fun DailySavingFeaturesBox(modifier: Modifier, stepsFeaturesDetails: StepsFeaturesDetails?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                colorResource(id = com.jar.app.core_ui.R.color.color_2D2840),
                RoundedCornerShape(8.dp)
            )
            .padding(top = 24.dp)
            .padding(bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            stepsFeaturesDetails?.featuresOrderText?.get(0)?.let {
                SpendTrackerScreenBoxLogo(
                    modifier.weight(1f), R.drawable.setting, body = it
                )
            }
            stepsFeaturesDetails?.featuresOrderText?.get(1)?.let {
                SpendTrackerScreenBoxLogo(
                    modifier.weight(1f), R.drawable.withdrawal, body = it
                )
            }
            stepsFeaturesDetails?.featuresOrderText?.get(2)?.let {
                SpendTrackerScreenBoxLogo(
                    modifier.weight(1f), R.drawable.gold_delivery, body = it
                )
            }
        }
    }
}

@Composable
fun DailySavingFeaturesBoxWithClickAction(
    onStopClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    onGetGoldClick: () -> Unit,
    screenData: DailyInvestmentSettingsData,
    analyticsHandler: AnalyticsApi
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .debounceClickable {
                        onStopClick()
                        analyticsHandler.postEvent(DSCancellation_PageClicked, mapOf(
                            Button_type to Stop_Icon
                        ))
                    }
                    .weight(1f)
                    .fillMaxWidth()
                    .background(
                        colorResource(id = com.jar.app.core_ui.R.color.color_2D2840),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(top = 24.dp)
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                screenData.stepsFeaturesDetails?.featuresOrderText?.let {
                    SpendTrackerScreenBoxLogo(
                        Modifier, R.drawable.setting, body = it.getOrDefault(0, stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.stop_anytime))
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .debounceClickable {
                        onWithdrawClick()
                        analyticsHandler.postEvent(DSCancellation_PageClicked, mapOf(
                            Button_type to Withdraw_Icon
                        ))
                    }
                    .weight(1f)
                    .fillMaxWidth()
                    .background(
                        colorResource(id = com.jar.app.core_ui.R.color.color_2D2840),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                screenData.stepsFeaturesDetails?.featuresOrderText?.let {
                    SpendTrackerScreenBoxLogo(
                        Modifier, R.drawable.withdrawal, body = it.getOrDefault(1, stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.withdraw_anytime))
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .debounceClickable {
                        onGetGoldClick()
                        analyticsHandler.postEvent(DSCancellation_PageClicked, mapOf(
                            Button_type to Gold_Delivery_Icon
                        ))
                    }
                    .weight(1f)
                    .fillMaxWidth()
                    .background(
                        colorResource(id = com.jar.app.core_ui.R.color.color_2D2840),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(top = 24.dp)
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                screenData.stepsFeaturesDetails?.featuresOrderText?.let {
                    SpendTrackerScreenBoxLogo(
                        Modifier, R.drawable.gold_delivery, body = it.getOrDefault(2, stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.get_gold_delivered_at_home))
                    )
                }
            }
        }
}


@Composable
fun SetupDetailOneRow(
    modifier: Modifier,
    source: String,
    text: String,
    lineShow: Boolean,
    painter: Painter? = null,
    onEditClick: () -> Unit,
    isDailySavingPaused: Boolean? = null,
    showStatusPill: Boolean? = null,
    setUpVersion: String? = null
) {

    Column {
        Row(
            modifier = modifier
                .background(colorResource(id = com.jar.app.core_ui.R.color.color_2E2942))
                .fillMaxWidth()
                .padding(top = 13.dp, bottom = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                fontFamily = jarFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3)
            )
            Text(
                text = source,
                modifier = Modifier.padding(end = 17.7.dp),
                fontFamily = jarFontFamily,
                fontSize = 14.sp,
                color = Color.White
            )

            if (showStatusPill == true) {
                when (isDailySavingPaused) {
                    true -> {
                        if (setUpVersion == DailyInvestmentCancellationEnum.V3.name || setUpVersion == DailyInvestmentCancellationEnum.V4.name) {
                            DsStatus(
                                text =
                                if (setUpVersion == DailyInvestmentCancellationEnum.V3.name)
                                    stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.stopped)
                                else
                                    stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.paused_status_pill)
                                ,
                                imageResource = R.drawable.stopped_icon,
                                backgroundColor = colorResource(id = com.jar.app.core_ui.R.color.color_EBB46A)
                            )
                        } else {
                            TransparentPillButtonWithImage(
                                imageResource = R.drawable.stopped_icon,
                                text = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.paused_status_pill),
                                backgroundColor = colorResource(id = com.jar.app.core_ui.R.color.color_EBB46A)
                            ) {

                            }
                        }
                    }

                    false -> {
                        if (setUpVersion == DailyInvestmentCancellationEnum.V3.name || setUpVersion == DailyInvestmentCancellationEnum.V4.name) {
                            DsStatus(
                                imageResource = R.drawable.active_status_icon,
                                text = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.active_status_pill),
                                backgroundColor = colorResource(id = com.jar.app.core_ui.R.color.color_58DDC8),
                            )
                        } else {
                            TransparentPillButtonWithImage(
                                imageResource = R.drawable.active_status_icon,
                                text = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.active_status_pill),
                                backgroundColor = colorResource(id = com.jar.app.core_ui.R.color.color_58DDC8),

                                ) {

                            }
                        }
                    }

                    else -> {
                        TransparentPillButtonWithImage(
                            imageResource = R.drawable.disabled_status_icon,
                            text = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.disabled_status_pill),
                            backgroundColor = colorResource(id = com.jar.app.core_ui.R.color.color_EB6A6E),

                            ) {

                        }
                    }
                }
            }

            if (painter != null) {
                Image(
                    painter = painter,
                    contentDescription = "",
                    modifier = Modifier
                        .debounceClickable { onEditClick() }
                )
            }
        }
        if (lineShow) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
                    .padding(end = 16.dp),
            ) {
                val startY = size.height - 1.dp.toPx()
                drawLine(
                    color = Color(0xFF3C3357),
                    start = Offset(0f, startY),
                    end = Offset(size.width, startY),
                    strokeWidth = 1.dp.toPx(),
                    alpha = 1f,
                )
            }
        }
    }
}
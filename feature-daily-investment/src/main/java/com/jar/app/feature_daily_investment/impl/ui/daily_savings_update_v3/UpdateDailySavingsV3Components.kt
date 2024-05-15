package com.jar.app.feature_daily_investment.impl.ui.daily_savings_update_v3

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.JarSecondaryButton
import com.jar.app.core_compose_ui.theme.JarColors
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.core_ui.R
import com.jar.app.feature_daily_investment.shared.domain.model.DailySavingsUpdateFlowValues
import com.jar.app.feature_daily_investment.shared.domain.model.UpdateDailyInvestmentStaticData
import com.jar.app.feature_mandate_payment_common.impl.model.UpiApp
import com.jar.app.feature_payment.impl.ui.payment_option.PayNowSection

@Composable
fun RenderToolbar(onBackPress:()->Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.bgColor))
            .padding(vertical = 18.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row {
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                "",
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        onBackPress.invoke()
                    },
                alignment = Alignment.CenterStart,
            )

            Spacer(
                modifier = Modifier.size(8.dp)
            )
            Text(
                text = stringResource(id = com.jar.app.feature_daily_investment.R.string.feature_daily_investment_update_edit_daily_saving_update_daily_savings),
                color = colorResource(id = R.color.white),
                fontWeight = FontWeight.W700,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontFamily = jarFontFamily,
            )
        }
    }
}

@Composable
fun UpdateDailySavingsScreen(
    modifier: Modifier,
    staticData: UpdateDailyInvestmentStaticData?,
    dailySavingsUpdateFlowValues: DailySavingsUpdateFlowValues?,
    onEditCLick: () -> Unit,
    ) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.color_121127))
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            painter = painterResource(id = com.jar.app.feature_daily_investment.R.drawable.feature_daily_investment_update_background),
            contentDescription = null
        )
        Column {
            Row {
                UpdateDailySavingsTopContainer(
                    modifier = Modifier,
                    staticData = staticData,
                    dailySavingsUpdateFlowValues = dailySavingsUpdateFlowValues,
                    onEditCLick = onEditCLick
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 100.dp, bottom = 5.dp),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CurrentDailySavingsJar(
                        staticData = staticData,
                        dailySavingsUpdateFlowValues = dailySavingsUpdateFlowValues,
                        )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ProjectedDailySavingsJar(
                        staticData = staticData,
                        dailySavingsUpdateFlowValues = dailySavingsUpdateFlowValues,
                        )
                }
            }
            Row(
                modifier = Modifier
                    .background(
                        color = colorResource(R.color.color_492B9D),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .align(Alignment.CenterHorizontally), // Center the Text horizontally
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 32.dp),
                    text = staticData?.savingsDuration.orEmpty(),
                    color = colorResource(id = R.color.color_EEEAFF),
                    fontWeight = FontWeight.W400,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontFamily = jarFontFamily
                )
            }
        }
    }
}

@Composable
fun UpdateDailySavingsTopContainer(
    modifier: Modifier,
    staticData: UpdateDailyInvestmentStaticData?,
    dailySavingsUpdateFlowValues: DailySavingsUpdateFlowValues?,
    onEditCLick: () -> Unit,
) {
    Column(
        modifier = modifier
            .background(JarColors.color_3C3357)
            .padding(horizontal = 16.dp, vertical = 18.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
        ) {
            Text(
                text = staticData?.subText.orEmpty(),
                color = colorResource(id = R.color.smallTxtColor),
                fontWeight = FontWeight.W400,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontFamily = jarFontFamily
            )
        }
        Row(
            modifier = modifier
                .padding(top = 4.dp)
        ) {
            val text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.White, fontWeight = FontWeight.W700, fontSize = 18.sp, fontFamily = jarFontFamily)) {
                    append(stringResource(id = com.jar.app.feature_daily_investment.R.string.feature_daily_investment_update_daily_saving_header_1))
                }
                dailySavingsUpdateFlowValues?.recommendedDailySavingsAmount?.let { amount ->
                    withStyle(style = SpanStyle(color = colorResource(id = R.color.color_ebb46a))) {
                        append(" ₹ ${amount.toInt()}/Day " )
                    }
                }
                withStyle(style = SpanStyle(color = Color.White, fontWeight = FontWeight.W700, fontSize = 18.sp, fontFamily = jarFontFamily)) {
                    append(stringResource(id = com.jar.app.feature_daily_investment.R.string.feature_daily_investment_update_daily_saving_header_2))
                }
            }
            Text(
                text = text,
                color = colorResource(id = R.color.white),
                fontWeight = FontWeight.W700,
                fontSize = 18.sp,
                lineHeight = 26.sp,
                fontFamily = jarFontFamily
            )
        }
        Row(
            modifier = modifier
                .padding(top = 12.dp)
        ) {
            JarSecondaryButton(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(42.dp),
                text = staticData?.editAmountButtonText.orEmpty(),
                color = Color(0xFF3C3357),
                icon = R.drawable.core_ui_ic_edit,
                isAllCaps = false,
                onClick = {
                    onEditCLick.invoke()
                }
            )
        }
    }
    Divider(
        color = Color(0x776E9433),
        thickness = 1.dp,
    )
}

@Composable
fun CurrentDailySavingsJar(
    staticData: UpdateDailyInvestmentStaticData?,
    dailySavingsUpdateFlowValues: DailySavingsUpdateFlowValues?,
    ) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row() {
            Text(
                text = "Current",
                color = colorResource(id = R.color.color_C5B0FF),
                fontWeight = FontWeight.W700,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = jarFontFamily
            )
        }
        Row(
            modifier = Modifier
                .padding(top = 11.dp)
        ) {
            Text(
                text = "₹${dailySavingsUpdateFlowValues?.currentDailySavingsAmount.orZero().toInt()} ",
                color = colorResource(id = R.color.white),
                fontWeight = FontWeight.W700,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                fontFamily = jarFontFamily
            )
            Text(
                text = stringResource(id = com.jar.app.feature_daily_investment.R.string.feature_daily_investment_update_daily_saving_per_day),
                color = colorResource(id = R.color.smallTxtColor),
                fontWeight = FontWeight.W400,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                fontFamily = jarFontFamily,
            )
        }
        Row(
            modifier = Modifier.padding(top=34.dp)
        ){
            DailySavingsJar(com.jar.app.feature_daily_investment.R.drawable.feature_daily_investment_current_jar, dailySavingsUpdateFlowValues?.currentDailySavingsProjection.orZero())
        }
    }
}

@Composable
fun ProjectedDailySavingsJar(
    staticData: UpdateDailyInvestmentStaticData?,
    dailySavingsUpdateFlowValues: DailySavingsUpdateFlowValues?,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row() {
            Text(
                text = if(dailySavingsUpdateFlowValues?.currentDailySavingsAmount.orZero().toInt() < dailySavingsUpdateFlowValues?.recommendedDailySavingsAmount.orZero().toInt()) staticData?.recommendedSavings?.header.orEmpty() else "Updated",
                color = if(dailySavingsUpdateFlowValues?.currentDailySavingsAmount.orZero().toInt() <= dailySavingsUpdateFlowValues?.recommendedDailySavingsAmount.orZero().toInt()) colorResource(id = R.color.white) else colorResource(id = R.color.color_EB6A6E),
                fontWeight = FontWeight.W700,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = jarFontFamily
            )
        }
        Row(
            modifier = Modifier
                .padding(top = 11.dp)
        ) {
            Text(
                text = "₹${dailySavingsUpdateFlowValues?.recommendedDailySavingsAmount.orZero().toInt()} ",
                color = colorResource(id = R.color.white),
                fontWeight = FontWeight.W700,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                fontFamily = jarFontFamily
            )

            Text(
                text = stringResource(id = com.jar.app.feature_daily_investment.R.string.feature_daily_investment_update_daily_saving_per_day),
                color = colorResource(id = R.color.smallTxtColor),
                fontWeight = FontWeight.W400,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                fontFamily = jarFontFamily,
            )
        }
        Row(
            modifier = Modifier.padding(top=34.dp)
        ){
            if(dailySavingsUpdateFlowValues?.currentDailySavingsAmount.orZero().toInt() < dailySavingsUpdateFlowValues?.recommendedDailySavingsAmount.orZero().toInt()){
                DailySavingsJar(com.jar.app.feature_daily_investment.R.drawable.feature_daily_investment_recommended_jar,dailySavingsUpdateFlowValues?.recommendedDailySavingsProjection.orZero())
            }else if(dailySavingsUpdateFlowValues?.currentDailySavingsAmount.orZero().toInt() == dailySavingsUpdateFlowValues?.recommendedDailySavingsAmount.orZero().toInt()){
                DailySavingsJar(com.jar.app.feature_daily_investment.R.drawable.feature_daily_investment_current_jar,dailySavingsUpdateFlowValues?.recommendedDailySavingsProjection.orZero())
            } else{
                DailySavingsJar(com.jar.app.feature_daily_investment.R.drawable.feature_daily_investment_recommended_jar_2,dailySavingsUpdateFlowValues?.recommendedDailySavingsProjection.orZero())
            }
        }
    }
}

@Composable
fun DailySavingsJar(jarImage: Int, savings: Float) {
    Box{
        Image(
            painter = painterResource(id = jarImage),
            contentDescription = null,
            modifier = Modifier
                .wrapContentHeight(),
            )

        Text(
            text = "₹${savings.toInt()}",
            color = colorResource(id = R.color.white),
            fontWeight = FontWeight.W700,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            fontFamily = jarFontFamily,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 65.dp)
        )
    }
}

@Composable
fun OneTapPayment(
    payNowCtaText: String?,
    appChooserText: String?,
    mandateUpiApp: UpiApp? = null,
    isMandate: Boolean = true,
    isEnabled: Boolean = true,
    onAppChooserClicked: (Boolean) -> Unit = {},
    onPayNowClicked: (Boolean) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.bgColor))
    ) {
        PayNowSection(
            payNowCtaText = payNowCtaText,
            appChooserText = appChooserText,
            mandateUpiApp= mandateUpiApp,
            isMandate = isMandate,
            isCtaEnabled = isEnabled,
            isAppChooserCtaEnabled = isEnabled,
            onAppChooserClicked = {onAppChooserClicked.invoke(it)},
            onPayNowClicked = {onPayNowClicked.invoke(it)},
            modifier = Modifier
                .height(80.dp)
                .padding(16.dp)
                .background(colorResource(id = R.color.bgColor))
        )
    }
}

@Composable
fun DefaultPayment(
    payNowCtaText: String?,
    onPayNowClicked: (Boolean) -> Unit = {},
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                colorResource(id = R.color.purple400)
            )
    ) {
        JarPrimaryButton(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            text = payNowCtaText.orEmpty(),
            onClick = {
                onPayNowClicked.invoke(true)
            })
    }

}




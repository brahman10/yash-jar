package com.jar.app.feature_lending.impl.ui.realtime_flow.bank_statement.confirm

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.core_ui.R
import com.jar.app.feature_lending.impl.ui.common_component.OutlinedTextFieldWithIcon
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi


@Preview
@Composable
fun EnterBankStatementPasswordBottomSheet(
    modifier: Modifier = Modifier,
    password: String = "",
    onValueChange: (String) -> Unit = { },
    onCrossClick: () -> Unit = { },
    onSubmitButtonClick: () -> Unit = { },
    analyticsApi: AnalyticsApi? = null
) {

    LaunchedEffect(key1 = Unit) {
        analyticsApi?.postEvent(
            LendingEventKeyV2.RLENDING_AFTERBANKSTATEMENTUPLOADFLOW,
            mapOf(
                LendingEventKeyV2.screen_name to LendingEventKeyV2.ENTER_PASSWORD_BS,
                LendingEventKeyV2.action to LendingEventKeyV2.ENTER_PASSWORD_BS_LAUNCHED
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colorResource(id = R.color.lightBgColor)
            )
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 19.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_enter_password.resourceId),
                fontSize = 18.sp,
                fontFamily = jarFontFamily,
                fontWeight = FontWeight(700),
                color = colorResource(id = R.color.commonTxtColor)
            )

            Image(
                modifier = Modifier.debounceClickable {
                    onCrossClick()
                },

                painter = painterResource(id = com.jar.app.feature_lending.R.drawable.feature_lending_bottom_sheet_cross_icon),
                contentDescription = "cross"
            )

        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            thickness = 1.dp,
            color = colorResource(id = R.color.color_3C3357)
        )
        Text(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp),
            text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_enter_bank_statement_password.resourceId),
            fontSize = 16.sp,
            fontFamily = jarFontFamily,
            color = Color.White
        )

        OutlinedTextFieldWithIcon(
            value = password,
            onValueChange = { newValue ->
               if (newValue.length <= 20) onValueChange(newValue)
            },
            placeholderText = "SOBI13123995",
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp),
            haveTrailingIcon = false


        )
        JarPrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 48.dp),
            text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_submit.resourceId),
            onClick = {
                onSubmitButtonClick()
            },
            isEnabled = true,
            isAllCaps = false
        )
    }
}
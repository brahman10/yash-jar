package com.jar.app.feature_lending.impl.ui.realtime_flow.bottom_sheet

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
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
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.RefreshCreditScoreBottomSheetUiState
import com.jar.app.feature_lending.shared.domain.ui_event.RefreshCreditScoreBottomSheetEvent
import com.jar.app.feature_lending.impl.ui.common_component.OutlinedTextFieldWithIcon

@Composable
fun RealTimeRefreshCreditScoreBottomSheet(
    modifier: Modifier = Modifier,
    uiState: RefreshCreditScoreBottomSheetUiState,
    onCrossClick: () -> Unit = {},
    onProceedButtonClick: () -> Unit = {},
    onPanChange: (RefreshCreditScoreBottomSheetEvent) -> Unit,
    onNameChange: (RefreshCreditScoreBottomSheetEvent) -> Unit,
    isCheckCreditScore: Boolean = false,
    isvisible: Boolean = false
) {
    BackHandler(enabled = isvisible) {
        onCrossClick()
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colorResource(id = com.jar.app.core_ui.R.color.bgColor)
            )
            .verticalScroll(rememberScrollState())
    ) {
        val focusManager = LocalFocusManager.current
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 16.dp, end = 16.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(com.jar.app.feature_lending.shared.MR.strings.feature_lending_enter_your_details.resourceId),
                fontSize = 18.sp,
                fontFamily = jarFontFamily,
                fontWeight = FontWeight(700),
                color = colorResource(id = com.jar.app.core_ui.R.color.commonTxtColor)
            )

            Image(
                modifier = Modifier.debounceClickable {
                    focusManager.clearFocus()
                    onCrossClick()
                },

                painter = painterResource(id = R.drawable.feature_lending_bottom_sheet_cross_icon),
                contentDescription = "cross"
            )

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_3E3953),
                    RoundedCornerShape(8.dp)
                )
        ) {
            Image(
                modifier = Modifier.padding(top = 18.dp, start = 12.dp, end = 8.dp),
                painter = painterResource(id = R.drawable.feature_lending_tick_icon),
                contentDescription = ""
            )
            Text(
                modifier = Modifier.padding(vertical = 16.dp),
                text = stringResource(com.jar.app.feature_lending.shared.MR.strings.feature_lending_please_ensure_that_these_details_are_correct.resourceId),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = jarFontFamily,
                color = colorResource(id = com.jar.app.core_ui.R.color.color_EEEAFF)
            )

        }
        Text(
            modifier = Modifier.padding(top = 28.dp, start = 16.dp),
            text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_name_as_per_aadhar.resourceId),
            fontSize = 16.sp,
            fontFamily = jarFontFamily,
            color = Color.White
        )

        OutlinedTextFieldWithIcon(
            value = uiState.name,
            onValueChange = {
                onNameChange(
                    RefreshCreditScoreBottomSheetEvent.OnNameUpdate(
                        it,
                        isCheckCreditScore
                    )
                )
            },
            placeholderText = "For eg, Suresh Singh",
            unfocusedBorderColor = colorResource(id = com.jar.app.core_ui.R.color.color_776E94),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp),
            haveTrailingIcon = false
        )
        Text(
            modifier = Modifier.padding(top = 28.dp, start = 16.dp),
            text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_mobile_number.resourceId),
            fontSize = 16.sp,
            fontFamily = jarFontFamily,
            color = Color.White
        )
        OutlinedTextFieldWithIcon(
            value = uiState.mobileNo,
            onValueChange = {},
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                .background(
                    shape = RoundedCornerShape(8.dp),
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_3E3953)
                ),
            placeholderText = "",
            readOnly = true,
            enabled = false,
            haveTrailingIcon = false,
            textColor = colorResource(id = com.jar.app.core_ui.R.color.color_80776E94),
            focusedBorderColor = colorResource(id = com.jar.app.core_ui.R.color.color_776E94),
            unfocusedBorderColor = colorResource(id = com.jar.app.core_ui.R.color.color_776E94)
        )
        Text(
            modifier = Modifier.padding(top = 12.dp, start = 16.dp),
            text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_information_cannot_change.resourceId),
            fontSize = 12.sp,
            fontFamily = jarFontFamily,
            color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3)
        )
        Text(
            modifier = Modifier.padding(top = 28.dp, start = 16.dp),
            text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_PAN_number.resourceId),
            fontSize = 16.sp,
            fontFamily = jarFontFamily,
            color = Color.White
        )
        OutlinedTextFieldWithIcon(
            value = uiState.panNo,
            onValueChange = {
                onPanChange(
                    RefreshCreditScoreBottomSheetEvent.OnPanUpdate(
                        it.uppercase(),
                        isCheckCreditScore
                    )
                )
            },
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp),
            placeholderText = "For eg, FGHPD2345L",
            showError = uiState.showPanError,
            errorMessage = stringResource(id = uiState.panErrorMessageId.resourceId),
            unfocusedBorderColor = colorResource(id = com.jar.app.core_ui.R.color.color_776E94),
            readOnly = uiState.isPanReadOnly,
            enabled = uiState.isPanReadOnly.not(),
            haveTrailingIcon = false
        )
        JarPrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 12.dp, top = 48.dp),
            text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_submit.resourceId),
            onClick = {
                focusManager.clearFocus()
                onProceedButtonClick()
            },
            isEnabled = uiState.isButtonEnabled,
            isAllCaps = false)
    }
}

@Preview
@Composable
fun PreviewChooseAmountComposable() {
    RealTimeRefreshCreditScoreBottomSheet(
        uiState = RefreshCreditScoreBottomSheetUiState(
            name = "",
            mobileNo = "",
            panNo = "",
            showPanError = false,
            panErrorMessageId = com.jar.app.feature_lending.shared.MR.strings.feature_lending_faqs,
            isButtonEnabled = true,
            isPanReadOnly = true
        ),
        onPanChange = {},
        onNameChange = {},
    )
}
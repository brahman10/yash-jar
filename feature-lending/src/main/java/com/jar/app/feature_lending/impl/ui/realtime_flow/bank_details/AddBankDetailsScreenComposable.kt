package com.jar.app.feature_lending.impl.ui.realtime_flow.bank_details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_compose_ui.theme.JarColors
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.core_compose_ui.utils.shimmerEffect
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.AddBankDetailsState
import com.jar.app.feature_lending.impl.ui.common_component.OutlinedTextFieldWithIcon
import com.jar.app.feature_lending.impl.ui.realtime_flow.components.RealTimeFlowSteps
import com.jar.app.feature_lending.shared.domain.ui_event.AddBankDetailsEvent

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AddBankDetailsScreen(
    modifier: Modifier = Modifier,
    uiState: AddBankDetailsState,
    onValueChangeOfAccountNo: (AddBankDetailsEvent) -> Unit,
    onValueChangeOfIfscCode: (AddBankDetailsEvent) -> Unit,
) {
    val uspAnnotatedText by remember(key1 = uiState.uspText) {
        mutableStateOf(createUspAnnotatedString(uiState.uspText))
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        uiState.realTimeBankDetailSteps?.let {
            RealTimeFlowSteps(
                modifier = Modifier
                    .background(Color(0xFF3E3953))
                    .padding(16.dp),
                realTimeUiSteps = it
            )
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color(0xFF3E3953))
                    .padding(16.dp)
                    .shimmerEffect()
            ) {

            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colorResource(id = com.jar.app.core_ui.R.color.lightBgColor),
                )
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.align(Alignment.CenterVertically),
                painter = painterResource(id = R.drawable.feature_lending_arrow),
                contentDescription = "",
                colorFilter = ColorFilter.tint(color = JarColors.light_green)
            )

            Text(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .align(Alignment.CenterVertically),
                text = uspAnnotatedText,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 10.sp,
                lineHeight = 17.sp
            )
        }
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 20.dp),
            text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_add_your_details.resourceId),
            fontSize = 20.sp,
            fontFamily = jarFontFamily,
            fontWeight = FontWeight(700),
            color = Color.White
        )
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp),
            text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_account_number.resourceId),
            fontSize = 16.sp,
            fontFamily = jarFontFamily,
            color = Color.White
        )
        OutlinedTextFieldWithIcon(
            value = uiState.bankAccountNumber,
            onValueChange = { newValue ->
                if (newValue.length <= 20) onValueChangeOfAccountNo(
                    AddBankDetailsEvent.updateAccountNo(
                        newValue.filter { it.isDigit() }
                    )
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            placeholderText = stringResource(com.jar.app.feature_lending.shared.MR.strings.feature_lending_bank_account_placeholder.resourceId),
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Text(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 12.dp),
            text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_ifsc_code.resourceId),
            fontSize = 16.sp,
            fontFamily = jarFontFamily,
            color = Color.White
        )

        OutlinedTextFieldWithIcon(
            value = uiState.ifscCode,
            onValueChange = { newValue ->
                if (newValue.length <= 11) {
                    onValueChangeOfIfscCode(AddBankDetailsEvent.updateIfscCode(newValue))
                } else {
                    onValueChangeOfIfscCode(AddBankDetailsEvent.updateIfscCode(newValue.take(11)))
                }
            },
            placeholderText = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_ifsc_placeholder.resourceId),
            modifier = Modifier.padding(horizontal = 16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            showError = uiState.errorInIfscCode,
            errorMessage = uiState.ifscCodeErrorMessage
        )

        if (uiState.bankAddress.isNotBlank() && uiState.bankImageUrl.isNotBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                GlideImage(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape),

                    model = uiState.bankImageUrl,
                    contentDescription = "bankImage"
                )
                Text(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .align(Alignment.CenterVertically),
                    text = "${uiState.bankName}, ${uiState.bankAddress}",
                    fontSize = 12.sp,
                    fontFamily = jarFontFamily,
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2)
                )
            }
        }


    }
}


fun createUspAnnotatedString(usp: String) = buildAnnotatedString {
    val words = usp.split(" ")
    if (words.size > 2) {
        append(words[0])
        addStyle(SpanStyle(fontWeight = FontWeight.ExtraBold), 0, words[0].length)
        append(" ")
        append(words.subList(1, words.size).joinToString(separator = " "))
    } else {
        append(usp)
    }
    toAnnotatedString()
}

@Preview
@Composable
fun PreviewAddBankDetailsScreen() {
    AddBankDetailsScreen(
        uiState = AddBankDetailsState(
            bankImageUrl = "https://i.imgur.com/wHD9rGN.jpeg",
            bankAddress = "Axis Bank, ANGUL - ORISSA, SHANKAR CINEMA \n ROAD, ANGUL, DIST - ANGUL ANGUL ORISSA\n 759122",
            errorInIfscCode = true,
            ifscCodeErrorMessage = "GG, HH, PP"
        ),
        onValueChangeOfAccountNo = {},
        onValueChangeOfIfscCode = {}
    )
}
package com.jar.app.feature_sell_gold.impl.ui.amount.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.base.util.volumeToString
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_2E2942
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_4B4366
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_7745FF
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_789BDE
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_ACA1D3FF
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_EB6A6E
import com.jar.app.core_compose_ui.theme.JarTypography

@Composable
fun AmountTextField(
    rupeeSymbol: String,
    modifier: Modifier = Modifier,
    goldWeight: Float,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    errorMessage: String,
    focusRequester: FocusRequester = remember { FocusRequester() },
    onAcquireFocus: (Boolean) -> Unit,
    expanded: Boolean
) {
    Box(modifier = modifier) {
        val focusManager = LocalFocusManager.current
        var hasFocus by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
        LaunchedEffect(expanded) {
            if (expanded) focusManager.clearFocus()
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .focusRequester(focusRequester)
                .onFocusChanged {
                    hasFocus = it.hasFocus
                    onAcquireFocus(it.hasFocus)
                }
                .shadow(
                    elevation = 4.dp,
                    spotColor = when {
                        errorMessage.isNotBlank() -> colorResource(id = color_EB6A6E.resourceId)
                        hasFocus || errorMessage.isBlank() -> colorResource(id = color_7745FF.resourceId)
                        else -> colorResource(id = color_4B4366.resourceId)
                    },
                    ambientColor = when {
                        errorMessage.isNotBlank() -> colorResource(id = color_EB6A6E.resourceId)
                        hasFocus || errorMessage.isBlank() -> colorResource(id = color_7745FF.resourceId)
                        else -> colorResource(id = color_4B4366.resourceId)
                    },
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.dp,
                    color = when {
                        errorMessage.isNotBlank() -> colorResource(id = color_EB6A6E.resourceId)
                        hasFocus || errorMessage.isBlank() -> colorResource(id = color_7745FF.resourceId)
                        else -> colorResource(id = color_4B4366.resourceId)
                    },
                    shape = RoundedCornerShape(16.dp)
                )
                .fillMaxWidth()
                .height(76.dp)
                .background(
                    color = colorResource(id = color_2E2942.resourceId),
                    shape = RoundedCornerShape(size = 16.dp)
                )
                .padding(
                    start = 44.dp,
                    top = 18.dp,
                    end = 122.dp,
                    bottom = 18.dp
                ),
            singleLine = true,
            cursorBrush = SolidColor(colorResource(id = color_789BDE.resourceId)),
            textStyle = JarTypography.h1.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() },
            )
        )
        Text(
            modifier = Modifier
                .padding(start = 20.dp)
                .align(Alignment.CenterStart),
            text = rupeeSymbol,
            textAlign = TextAlign.Start,
            style = JarTypography.h1.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                baselineShift = BaselineShift(-0.15f)
            ),
            color = Color.White
        )
        Text(
            modifier = Modifier
                .padding(end = 20.dp)
                .width(82.dp)
                .align(Alignment.CenterEnd),
            text = if (goldWeight >= 1.0) {
                "${goldWeight.volumeToString()} g"
            } else {
                "${goldWeight.volumeToString()} mg"
            },
            textAlign = TextAlign.End,
            style = JarTypography.body2.copy(
                lineHeight = 20.sp,
                baselineShift = BaselineShift(-0.10f)
            ),
            color = colorResource(id = color_ACA1D3FF.resourceId)
        )
    }
}

@Preview
@Composable
fun AmountTextFieldPreview() {
    AmountTextField(
        rupeeSymbol = "₹",
        goldWeight = 0.0013f,
        value = TextFieldValue(text = "35"),
        onValueChange = {},
        errorMessage = "",
        onAcquireFocus = {},
        expanded = true
    )
}
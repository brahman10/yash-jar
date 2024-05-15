package com.jar.gold_redemption.impl.ui.search_store.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.feature_gold_redemption.R
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_VStateBSTyped
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.TYPED_TEXT
import kotlinx.coroutines.launch

@Composable
@Preview
fun RenderSearchFieldPreview() {
    val x =  remember { mutableStateOf<String>("Uttar pradesh") }
    RenderSearchField(searchText = x, analyticsFunction = { it, map -> })
}
@Composable
@Preview
fun RenderSearchFieldPreview2() {
    val x =  remember { mutableStateOf<String>("") }
    RenderSearchField(searchText = x, analyticsFunction = { it, map -> })
}
@Composable
fun RenderSearchField(searchText: MutableState<String>, analyticsFunction: (it: String, map: Map<String, String>) -> Unit) {
    var lastClickTime by remember { mutableStateOf(0L) }
    val courtineContext = rememberCoroutineScope()
    val debounceInterval = 500L
    Row (Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Row(
            Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
                .background(
                    colorResource(id = com.jar.app.core_ui.R.color.color_3C3357),
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = com.jar.app.core_ui.R.drawable.ic_search),
                contentDescription = "",
                modifier = Modifier
                    .size(30.dp),
                tint = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2)
            )
            CustomTextField(
                modifier = Modifier
                    .background(
                        colorResource(id = com.jar.app.core_ui.R.color.color_3C3357)
                    )
                    .weight(1f),
                value = searchText.value,
                onValueChange = {
                    searchText.value = it
                    courtineContext.launch {
                        val currentTime = System.currentTimeMillis()
                        if ((currentTime - lastClickTime) < debounceInterval) return@launch
                        lastClickTime = currentTime
                        analyticsFunction(
                            Redemption_VStateBSTyped,
                        mapOf(
                            TYPED_TEXT to it
                        )

                        )
                    }
                },
                placeholder = {
                    Text(
                        text = stringResource(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_search_state),
                        color = colorResource(id = com.jar.app.core_ui.R.color.color_776E94)
                    )
                },
                textStyle = JarTypography.body1,
                colors = TextFieldDefaults.textFieldColors(
                    textColor = colorResource(id = com.jar.app.core_ui.R.color.color_EEEAFF),
                    backgroundColor = colorResource(id = com.jar.app.core_ui.R.color.color_3C3357),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
        }
        if (!searchText.value.isNullOrBlank()) {
            Text(
                text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_cancel),
                style = JarTypography.body1,
                color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2),
                modifier = Modifier.debounceClickable {
                    searchText.value = ""
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape =
        MaterialTheme.shapes.small.copy(bottomEnd = ZeroCornerSize, bottomStart = ZeroCornerSize),
    colors: TextFieldColors = TextFieldDefaults.textFieldColors()
) {
    // If color is not provided via the text style, use content color as a default
    val textColor = textStyle.color.takeOrElse {
        colors.textColor(enabled).value
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    @OptIn(ExperimentalMaterialApi::class)
    (BasicTextField(
        value = value,
        modifier = modifier
            .background(colors.backgroundColor(enabled).value, shape)
            .indicatorLine(enabled, isError, interactionSource, colors),
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = mergedTextStyle,
        cursorBrush = SolidColor(colors.cursorColor(isError).value),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = maxLines,
        decorationBox = @Composable { innerTextField ->
            // places leading icon, text field with label and placeholder, trailing icon
            TextFieldDefaults.TextFieldDecorationBox(
                value = value,
                visualTransformation = visualTransformation,
                innerTextField = innerTextField,
                placeholder = placeholder,
                label = label,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                singleLine = singleLine,
                enabled = enabled,
                isError = isError,
                interactionSource = interactionSource,
                colors = colors
            )
        }
    ))
}
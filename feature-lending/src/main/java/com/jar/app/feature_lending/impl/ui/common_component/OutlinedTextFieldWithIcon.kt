package com.jar.app.feature_lending.impl.ui.common_component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.jarFontFamily

@Composable
fun OutlinedTextFieldWithIcon(
    modifier: Modifier = Modifier,
    value: String,
    placeholderText: String,
    textColor: Color = colorResource(id = com.jar.app.core_ui.R.color.white),
    onValueChange: (String) -> Unit,
    placeHolderTextColor: Color = colorResource(id = com.jar.app.core_ui.R.color.color_776e94),
    haveTrailingIcon: Boolean = true,
    trailingIcon: Painter = painterResource(id = com.jar.app.feature_lending.R.drawable.feature_lending_ic_close_white),
    focusedBorderColor: Color = colorResource(id = com.jar.app.core_ui.R.color.color_C5B0FF),
    unfocusedBorderColor: Color = colorResource(id = com.jar.app.core_ui.R.color.color_776e94),
    showError: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    errorMessage: String = stringResource(id = com.jar.app.feature_lending.shared.R.string.feature_lending_email_error),
    errorIcon: Painter = painterResource(id = com.jar.app.feature_lending.R.drawable.feature_lending_ic_error),
    errorColor: Color = colorResource(id = com.jar.app.core_ui.R.color.redAlertText),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    cursorColor: Color = colorResource(id = com.jar.app.core_ui.R.color.color_C5B0FF),
    leadingIconView: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholderText,
                    color = if (showError) errorColor else placeHolderTextColor
                )
            },
            value = value,
            onValueChange = onValueChange,
            shape = RoundedCornerShape(8.dp),
            leadingIcon = leadingIconView,
            trailingIcon = {
                if (haveTrailingIcon) {
                    if (value.trim().isNotEmpty()) {
                        IconButton(onClick = { }) {
                            Image(
                                modifier = Modifier
                                    .size(20.dp)
                                    .debounceClickable {
                                        onValueChange("")
                                    },
                                painter = trailingIcon,
                                contentDescription = "",
                                contentScale = ContentScale.Inside
                            )
                        }
                    }
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = textColor,
                focusedBorderColor = if (showError) errorColor else if (readOnly) unfocusedBorderColor else focusedBorderColor,
                unfocusedBorderColor = if (showError) errorColor else unfocusedBorderColor,
                cursorColor = cursorColor,
                disabledTextColor = if (readOnly) textColor else textColor.copy(ContentAlpha.disabled)
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            enabled = enabled,
            readOnly = readOnly,
            singleLine = true
        )

        if (showError) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    modifier = Modifier
                        .size(16.dp),
                    painter = errorIcon,
                    contentDescription = "",
                    contentScale = ContentScale.Inside
                )

                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = errorMessage,
                    fontSize = 12.sp,
                    fontFamily = jarFontFamily,
                    color = errorColor
                )
            }
        }
    }
}

@Preview
@Composable
fun OutlinedTextFieldWithIconPreview() {
    //Outlined TextField With Error
    OutlinedTextFieldWithIcon(
        value = "",
        onValueChange = {},
        placeholderText = "XXXXX-0000"
    )
}

@Preview
@Composable
fun OutlinedTextFieldWithTrailingIconPreview() {

    //Outlined TextField With Trailing Icon
    OutlinedTextFieldWithIcon(
        value = "Dummy text",
        onValueChange = {},
        placeholderText = "XXXXX-0000",
        trailingIcon = painterResource(id = com.jar.app.feature_lending.R.drawable.feature_lending_ic_close_white)
    )
}

@Preview
@Composable
fun OutlinedTextFieldWithLeadingIconPreview() {

    //Outlined TextField With Leading Icon
    val text = remember { mutableStateOf("") }

    val iconView = @Composable {
        Icon(
            painter = painterResource(id = com.jar.app.feature_lending.R.drawable.feature_lending_ic_close_white),
            contentDescription = null
        )
    }
    OutlinedTextFieldWithIcon(
        value = text.value,
        onValueChange = { text.value = it },
        placeholderText = "ZZZZ-9999",
        trailingIcon = painterResource(id = com.jar.app.feature_lending.R.drawable.feature_lending_ic_close_white),
        leadingIconView = if (text.value.isEmpty()) iconView else null
    )
}

@Preview
@Composable
fun OutlinedTextFieldWithOutTrailingIconPreview() {

    //Outlined TextField With Trailing Icon
    OutlinedTextFieldWithIcon(
        value = "Dummy text",
        onValueChange = {},
        placeholderText = "XXXXX-0000",
        haveTrailingIcon = false
    )
}

@Preview
@Composable
fun OutlinedTextFieldWithErrorPreview() {

    Column {
        //Outlined TextField With Error
        OutlinedTextFieldWithIcon(
            value = "Dummy text",
            onValueChange = {},
            placeholderText = "XXXXX-0000-X",
            showError = true,
            errorMessage = stringResource(id = com.jar.app.feature_lending.shared.R.string.feature_lending_email_error),
            errorIcon = painterResource(id = com.jar.app.feature_lending.R.drawable.feature_lending_ic_error),
            errorColor = colorResource(id = com.jar.app.core_ui.R.color.redAlertText)
        )

        //Outlined TextField With Error
        OutlinedTextFieldWithIcon(
            modifier = Modifier.padding(top = 12.dp),
            value = "",
            onValueChange = {},
            placeholderText = "XXXXX-0000",
            showError = true,
            errorMessage = stringResource(id = com.jar.app.feature_lending.shared.R.string.feature_lending_email_error),
            errorIcon = painterResource(id = com.jar.app.feature_lending.R.drawable.feature_lending_ic_error),
            errorColor = colorResource(id = com.jar.app.core_ui.R.color.redAlertText)
        )
    }
}






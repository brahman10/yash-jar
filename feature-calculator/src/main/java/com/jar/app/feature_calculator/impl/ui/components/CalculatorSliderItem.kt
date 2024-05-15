package com.jar.app.feature_calculator.impl.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.base.util.isNumber
import com.jar.app.core_compose_ui.component.AutoResizeTextView
import com.jar.app.core_compose_ui.theme.JarColors
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.feature_calculator.R
import com.jar.app.feature_calculator.shared.domain.model.CalculatorType
import com.jar.app.feature_calculator.shared.domain.model.SliderSubType
import com.jar.app.feature_calculator.shared.domain.model.SliderType
import com.jar.app.feature_lending.shared.MR
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorSliderItem(
    modifier: Modifier,
    title: String,
    currentValue: Float,
    minValue: Float,
    maxValue: Float,
    stepCount: Float,
    minTitle: String,
    maxTitle: String,
    onSliderChange: (newValue: Float) -> Unit,
    onTextChange: (newValue: String) -> Unit,
    toggleState: SliderSubType,
    onToggle: (defaultValue: Float, state: SliderSubType) -> Unit,
    showToggle: Boolean,
    type: SliderType,
    calculatorType: CalculatorType
) {
    Column(
        modifier = modifier.then(other = Modifier.fillMaxWidth())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            AutoResizeTextView(
                modifier = Modifier.wrapContentWidth(),
                text = title,
            )

            if (showToggle) {
                SwitchComponent(modifier = Modifier
                    .wrapContentSize()
                    .padding(start = 12.dp), toggleState, onStateToggled = {
                    onToggle(
                        minValue,
                        it)
                })
            }

            Spacer(modifier = Modifier.weight(1f))
            if(calculatorType != CalculatorType.SAVINGS_CALCULATOR) {
                BasicTextFieldWithCursorAtEnd(
                    modifier = Modifier
                        .defaultMinSize(minWidth = 100.dp)
                        .background(
                            color = JarColors.color_2E2942,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    value = when (type) {
                        SliderType.AMOUNT -> currentValue.toInt().toString()
                        SliderType.TENURE -> currentValue.toInt().toString()
                        SliderType.PERCENTAGE -> stringResource(
                            id = MR.strings.feature_lending_float_prefix_two_digit.resourceId,
                            currentValue
                        )

                        SliderType.NONE -> currentValue.toString()
                    },
                    onValueChange = { formattedString ->
                        if (formattedString.length > 12)
                            return@BasicTextFieldWithCursorAtEnd
                        formattedString.replace(",", "").let {
                            if (it.isNumber()) {
                                onTextChange(it)
                            }
                        }
                    },
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.End,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = jarFontFamily
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    enabled = true,
                    cursorBrush = SolidColor(JarColors.color_ACA1d3),
                    visualTransformation = if (type == SliderType.AMOUNT) NumberCommaTransformation() else VisualTransformation.None
                )
            }else{
                val value =  when (type) {
                    SliderType.AMOUNT -> "₹ ${currentValue.toInt()}"
                    SliderType.TENURE -> {
                        if(toggleState == SliderSubType.YEAR){
                            "${currentValue.toInt()} Y"
                        } else{
                            "${currentValue.toInt()} M"
                        }
                    }

                    SliderType.PERCENTAGE -> stringResource(
                        id = MR.strings.feature_lending_float_prefix_two_digit.resourceId,
                        currentValue
                    )

                    SliderType.NONE -> currentValue.toString()
                }

                Text(
                    text = value,
                    style = JarTypography.body2.copy(fontWeight = FontWeight.Bold,color = Color.White)
                )
            }

        }
        if(calculatorType != CalculatorType.SAVINGS_CALCULATOR || type != SliderType.PERCENTAGE) {
            Slider(modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth()
                    .semantics { contentDescription = "" }
                    .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
                value = currentValue,
                onValueChange = onSliderChange,
                valueRange = minValue..maxValue,
                steps = max((((maxValue - minValue) / stepCount).toInt() - 1), 1),
                colors = SliderDefaults.colors(
                    inactiveTrackColor = JarColors.color_3C3357,
                    activeTrackColor = Color.White,
                ),
                thumb = {
                    Icon(
                        painter = painterResource(id = R.drawable.feature_calculator_ic_slider_thumb),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                },
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = minTitle,
                    color = JarColors.color_776E94,
                    fontFamily = jarFontFamily,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Start
                )

                Text(
                    modifier = Modifier.weight(1f),
                    text = maxTitle,
                    color = JarColors.color_776E94,
                    fontFamily = jarFontFamily,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}


class NumberCommaTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            text = AnnotatedString(text.text.toLongOrNull().formatWithComma()),
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return text.text.toLongOrNull().formatWithComma().length
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return text.length
                }
            }
        )
    }
}

fun Long?.formatWithComma(): String =
    NumberFormat.getNumberInstance(Locale.getDefault()).format(this ?: 0)
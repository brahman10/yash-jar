package com.jar.app.feature_calculator.impl.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.feature_calculator.R
import com.jar.app.feature_calculator.shared.domain.model.SliderSubType

@Composable
fun SwitchComponent(
    modifier: Modifier,
    state: SliderSubType,
    onStateToggled: (state: SliderSubType) -> Unit
) {
    Row(
        modifier = modifier.then(
            other = Modifier
                .background(color = Color(0xFF3C3357), shape = RoundedCornerShape(size = 16.dp))
                .padding(start = 4.dp, top = 4.dp, end = 4.dp, bottom = 4.dp)
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .background(
                    color = if (state == SliderSubType.MONTH) Color(0xFFACA1D3) else Color(0xFF3C3357),
                    shape = RoundedCornerShape(size = 12.8.dp)
                )
                .debounceClickable {
                    onStateToggled(SliderSubType.MONTH)
                }
                .padding(start = 12.dp, top = 4.dp, end = 12.dp, bottom = 4.dp),
            text = if (state == SliderSubType.MONTH) stringResource(R.string.month) else SliderSubType.MONTH.affix,
            style = TextStyle(
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontFamily = jarFontFamily,
                fontWeight = FontWeight.Normal,
                color = if (state == SliderSubType.MONTH) Color(0xFF272239) else Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
            )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            modifier = Modifier
                .background(
                    color = if (state == SliderSubType.YEAR) Color(0xFFACA1D3) else Color(0xFF3C3357),
                    shape = RoundedCornerShape(size = 12.8.dp)
                )
                .debounceClickable {
                    onStateToggled(SliderSubType.YEAR)
                }
                .padding(start = 12.dp, top = 4.dp, end = 12.dp, bottom = 4.dp),
            text = if (state == SliderSubType.YEAR) stringResource(R.string.year) else SliderSubType.YEAR.affix,
            style = TextStyle(
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontFamily = jarFontFamily,
                fontWeight = FontWeight.Normal,
                color = if (state == SliderSubType.YEAR) Color(0xFF272239) else Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
            )
        )
    }
}

@Preview
@Composable
fun SwicthPreview() {
    SwitchComponent(
        modifier = Modifier.wrapContentSize(),
        SliderSubType.MONTH, {

        }
    )
}
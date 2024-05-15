package com.jar.app.feature_calculator.impl.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.theme.jarFontFamily

@Composable
fun KeyValueDetails(
    modifier: Modifier,
    key: String,
    value: String
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = key,
            style = TextStyle(
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontFamily = jarFontFamily,
                fontWeight = FontWeight.Normal,
                color = Color.White,
            )
        )

        Text(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            text = value,
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 12.sp,
                fontFamily = jarFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        )
    }
}

@Preview
@Composable
fun KeyValueDetailsPreview() {
    KeyValueDetails(modifier = Modifier, key = "Amount", value = "50000")
}
package com.jar.app.core_compose_ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.utils.generateAnnotatedFromHtmlString


@Composable
fun TagWithShadow(modifier: Modifier = Modifier, text: String, color: Color, shadowColor: Color) {
    Row(
        modifier = modifier
            .padding(end = 2.dp)
            .background(
                shadowColor,
                shape = RoundedCornerShape(topEnd = 13.dp, bottomEnd = 13.dp)
            )
            .graphicsLayer {
                translationX = -2.dp.value
            }
    ) {
        Text(
            text.generateAnnotatedFromHtmlString(),
            color = Color.White,
            modifier = Modifier
                .padding(end = 5.dp)
                .background(
                    color,
                    shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                )
                .padding(start = 4.dp, top = 4.dp, bottom = 4.dp, end = 5.dp),
            style = JarTypography.body1,
            fontSize = 12.sp
        )
    }
}


@Composable
@Preview
fun TagWithShadowPreview() {
    Box(
        Modifier
            .background(Color.White)
            .padding(end = 20.dp, top = 20.dp, bottom = 20.dp)) {
        TagWithShadow(
            text = "+ <b>8.25%</b> extra gold",
            color = Color(0xFF1EA787),
            shadowColor = Color(0xFF58DDC8)
        )
    }
}
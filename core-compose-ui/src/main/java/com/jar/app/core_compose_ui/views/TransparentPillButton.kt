package com.jar.app.core_compose_ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography

@Composable
fun TransparentPillButton(text: String, color: Color, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .debounceClickable {
                onClick()
            },
        color = color,
        style = JarTypography.body2.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp)
    )
}

@Composable
fun TransparentPillButtonWithImage(
    imageResource: Int,
    cornerRadius: Dp = 6.dp,
    text: String,
    backgroundColor: Color,
    function: () -> Unit,
) {

    Row(
        modifier = Modifier
                .background(
                    color = backgroundColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(cornerRadius),
                )
            .debounceClickable {function()}
        .padding(top = 4.dp, bottom = 4.dp)
        ,
    ) {
        Image(
            painter = painterResource(id = imageResource), contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp)
        )

        Text(
            text = text,
            modifier = Modifier
                .padding(start = 4.dp, end = 8.dp),
            color = backgroundColor,
            style = if (imageResource != null) JarTypography.h6 else JarTypography.h6.copy(
                fontWeight = FontWeight.W700,
                fontSize = 12.sp
            )
        )
    }
}

@Preview
@Composable
fun TransparentPillButtonPreview() {
    TransparentPillButton(
        text = "Hello",
        color = colorResource(id = com.jar.app.core_ui.R.color.color_EBB46A)
    ) {}
}
package com.jar.app.core_compose_ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.R
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.theme.jarFontFamily


@Preview
@Composable
fun ScreenTopBannerForRestartTest() {
    ScreenTopBannerForRestart(
        onClick = {

        },
        logo = R.drawable.core_ui_hourglass,
        logoBg = Color(0xFFF19A9C),
        backgroundColor = Color(0xFFEB6A6E),
        performTaskString = "Setup Daily Savings",
        performTaskStringColor = Color.White,
        text = "Restart your daily savings",
        textColor = Color.White
    )
}

@Composable
fun ScreenTopBannerForRestart(
    onClick: () -> Unit,
    logo: Int = R.drawable.core_ui_hourglass,
    logoBg: Color = Color(0xFFF19A9C),
    backgroundColor: Color = Color(0xFFEB6A6E),
    performTaskString: String,
    performTaskStringColor: Color = Color.White,
    text: String,
    textColor: Color = Color.White
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(start = 14.dp)
            .padding(end = 14.dp)
            .padding(top = 12.dp)
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircleShapeLogoWithImage(color = logoBg, imageResource = logo)

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontFamily = jarFontFamily,
            fontWeight = FontWeight.W600,
            lineHeight = 18.sp,
            color = textColor,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = performTaskString,
            lineHeight = 14.sp,
            fontSize = 14.sp,
            color = performTaskStringColor,
            fontWeight = FontWeight.W600,
            modifier = Modifier.debounceClickable {
                onClick()
            },
            style = JarTypography.body1.copy(textDecoration = TextDecoration.Underline)
        )
    }
}

@Composable
fun CircleShapeLogoWithImage(color: Color, imageResource: Int) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(color = color),
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(id = imageResource), contentDescription = "")

    }
}
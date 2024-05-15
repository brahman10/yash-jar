package com.jar.app.core_compose_ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.base.util.getNameInitials
import com.jar.app.core_compose_ui.theme.JarTypography

@Composable
@Preview
fun InitialIconPreview() {
    AvatarIconCompose("Test Name")
}
@Composable
@Preview
fun AvatarIconComposeNullPreview() {
    AvatarIconCompose("+91123", true)
}
@Composable
fun AvatarIconCompose(title: String, displayAvatarIconIfInitialNonDigit: Boolean = false) {
    val text: String = remember { getNameInitials(title) }
    if (displayAvatarIconIfInitialNonDigit && text.getOrNull(0)?.isLetter() != true) {
        Image(
            painterResource(id = com.jar.app.core_ui.R.drawable.core_ui_common_avatar),
            contentDescription = "",
            modifier = Modifier.size(48.dp)
        )
    } else {
        Text(
            text = text,
            color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
            modifier = Modifier
                .size(48.dp)
                .background(
                    colorResource(id = com.jar.app.core_ui.R.color.color_272239),
                    shape = CircleShape
                )
                .padding(8.dp)
                .wrapContentHeight(),
            style = JarTypography.h6,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
        )
    }
}



package com.jar.app.feature_goal_based_saving.impl.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.JarSecondaryButton
import com.jar.app.core_ui.widget.OverlappingProfileView

@Composable
internal fun ColumnScope.CloseButton(modifier: Modifier = Modifier) {
    Icon(
        Icons.Filled.Close,
        contentDescription = "Close",
        tint = Color.White,
        modifier = modifier
            .size(24.dp)
            .align(Alignment.End)
    )
}

@Composable
fun ColumnScope.TitleText(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 36.sp,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )
}

@Preview
@Composable
fun ColumnScope.SubtitleText(text: String = "SubtitleText") {
    Text(
        text = text,
        color = Color.White,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Preview
@Composable
fun ColumnScope.Illustration(imageUrl: String = "") {
    GlideImage(
        model = imageUrl,
        contentDescription = "Illustration",
        modifier = Modifier
            .size(160.dp)
            .align(Alignment.CenterHorizontally)
    )
}

@Preview
@Composable
fun RowScope.TrustText(text: String = "Dummy text") {
    Text(
        text = text,
        color = Color(0xFFD5CDF2),
        modifier = Modifier
            .padding(start = 10.dp)
            .align(Alignment.CenterVertically)
    )
}

@Preview
@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    text: String = "Button Text",
    buttonType: String = "primaryButton",
    buttonBorderWidth: Dp = 0.dp,
    buttonStrokeColor: Color = Color.Transparent,
    buttonBackgroundColor: Color = Color.White,
    onClick:()->Unit = {}
) {
    if (buttonType == "primaryButton")
        JarPrimaryButton(text = text, modifier = modifier, onClick = { onClick.invoke() })
    else
        JarSecondaryButton(text = text, modifier = modifier, onClick = {
            onClick.invoke()
        })
}

@Composable
fun ColumnScope.OverlappingProfileViewWithTrustText() {
    Box(
        modifier = Modifier
            .align(Alignment.CenterHorizontally),
        contentAlignment = Alignment.CenterEnd) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.Center)
        ) {
            AndroidView(factory = {
                OverlappingProfileView(it)
            })
            TrustText(
                text = "20L+ Jar users save in Gold everyday",
            )
        }
    }
}



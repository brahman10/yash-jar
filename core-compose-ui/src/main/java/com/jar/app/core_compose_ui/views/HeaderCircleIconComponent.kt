package com.jar.app.core_compose_ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.core_compose_ui.component.JarImage
import com.jar.app.core_ui.R


@Composable
@Preview
fun HeaderCircleIconComponentPreview() {
    val cardBg = colorResource(id = com.jar.app.core_ui.R.color.color_3c3357)
    val defaultModifier: Modifier =
        Modifier
            .padding(
                horizontal = 12.dp,
            )
            .background(cardBg)

    HeaderCircleIconComponent(
        defaultModifier,
        finalStatus = R.drawable.core_ui_icon_check_filled,
        topPadding = 20.dp
    ) { }
}

@Composable
fun HeaderCircleIconComponent(
    defaultModifier: Modifier = Modifier,
    finalStatus: Int,
    topPadding: Dp = 10.dp,
    renderCelebratationLottie: @Composable () -> Unit,
) {
    BoxWithConstraints(
        defaultModifier
            .fillMaxWidth()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(
                    colorResource(id = R.color.color_272239),
                ).padding(top = topPadding), verticalAlignment = Alignment.Bottom
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .aspectRatio(2f)
                    .background(
                        colorResource(id = R.color.color_3C3357),
                        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                    )
            ) {

            }
            Box(
                Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            ) {
                Column(Modifier.fillMaxSize()) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(colorResource(id = R.color.color_272239))
                    )
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(colorResource(id = R.color.color_3C3357))
                    )
                }

            }
            Column(
                Modifier
                    .weight(1f)
                    .aspectRatio(2f)
                    .background(
                        colorResource(id = R.color.color_3C3357),
                        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                    )
            ) {

            }

        }
        Box(
            Modifier
                .padding(top = topPadding)
                .width(this.maxWidth / 3)
                .aspectRatio(1f)
                .zIndex(10f)
                .align(Alignment.Center)
        ) {
            Image(
                painter = painterResource(id = finalStatus),
                contentDescription = "",
                Modifier
                    .fillMaxSize()
                    .border(
                        10.dp,
                        colorResource(id = R.color.color_272239),
                        CircleShape
                    )
                    .zIndex(12f)
            )
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(this.maxWidth / 6 + 97.dp)
            .align(Alignment.TopCenter), contentAlignment = Alignment.BottomCenter) {
            renderCelebratationLottie()
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun HeaderCircleIconComponentWithUrl(
    defaultModifier: Modifier = Modifier,
    finalStatus: String,
    topPadding: Dp = 10.dp,
    renderCelebratationLottie: @Composable () -> Unit,
) {
    BoxWithConstraints(
        defaultModifier
            .fillMaxWidth()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(
                    colorResource(id = R.color.color_272239),
                ).padding(top = topPadding), verticalAlignment = Alignment.Bottom
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .aspectRatio(2f)
                    .background(
                        colorResource(id = R.color.color_3C3357),
                        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                    )
            ) {

            }
            Box(
                Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            ) {
                Column(Modifier.fillMaxSize()) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(colorResource(id = R.color.color_272239))
                    )
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(colorResource(id = R.color.color_3C3357))
                    )
                }

            }
            Column(
                Modifier
                    .weight(1f)
                    .aspectRatio(2f)
                    .background(
                        colorResource(id = R.color.color_3C3357),
                        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                    )
            ) {

            }

        }
        Box(
            Modifier
                .padding(top = topPadding)
                .width(this.maxWidth / 3)
                .aspectRatio(1f)
                .zIndex(10f)
                .align(Alignment.Center)
        ) {
            JarImage(
                imageUrl = finalStatus,
                contentDescription = "",
                Modifier
                    .fillMaxSize()
                    .border(
                        10.dp,
                        colorResource(id = R.color.color_272239),
                        CircleShape
                    )
                    .zIndex(12f)
            )
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(this.maxWidth / 6 + 97.dp)
            .align(Alignment.TopCenter), contentAlignment = Alignment.BottomCenter) {
            renderCelebratationLottie()
        }
    }
}



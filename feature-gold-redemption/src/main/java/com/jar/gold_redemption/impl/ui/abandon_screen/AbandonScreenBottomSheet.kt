package com.jar.gold_redemption.impl.ui.abandon_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.JarSecondaryButton
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.views.GradientSeperator
import com.jar.app.core_compose_ui.views.OverlappingProfileViewCompose
import com.jar.app.core_ui.R
import com.jar.app.feature_gold_redemption.shared.data.network.model.AbandonScreenData

@Composable
@Preview
fun AbandonScreenPreview() {
    AbandonScreenBottomSheet(
        AbandonScreenData(
            "",
            "Not now",
            "Continue",
            "",
            "",
            "",
            "Hello Name",
            listOf(""),
            "",
            "",
        ), {}, {}
    )
}


@Composable
internal fun AbandonScreenBottomSheet(abandonScreenData: AbandonScreenData
, continuePressed: () -> Unit, notNowPressed: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 1.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.color_2e2942))
                .border(
                    width = 0.5.dp, brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.04f to Color(0xFF93722F),
                            0.2f to Color(0xFF9C8350),
                            0.5f to Color(0xFFDBC28F),
                            0.65f to Color(0xFFBFA673),
                            0.82f to Color(0xFF886F3C),
                            0.98f to Color(0xFF765E2A)
                        ),
                        tileMode = TileMode.Mirror
                    ), shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .align(Alignment.TopStart),
        ) {
            Text(
                text = abandonScreenData.header ?: (stringResource(
                    id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_abandon_title,
                    "Name here"
                )),
                style = JarTypography.h6,
                color = colorResource(
                    id = R.color.white
                ),
                fontSize = 24.sp
            )
            Spacer(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth()
            )
            abandonScreenData.title1?.let {
                RenderBulletRow(it)
                Spacer(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth()
                )
            }
            abandonScreenData.title2?.let {
                RenderBulletRow(it)
                Spacer(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth()
                )
            }
            GradientSeperator(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            )
            Spacer(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth()
            )
            Row(Modifier, verticalAlignment = Alignment.CenterVertically) {
                abandonScreenData?.profileImages?.let {
                    OverlappingProfileViewCompose(list = it, size = 16.dp)
                }
                Text(
                    abandonScreenData.footerText.orEmpty(),
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
                    style = JarTypography.body1,
                    modifier = Modifier.padding(start = 12.dp),
                    fontSize = 12.sp
                )
            }
            JarPrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_continue),
                onClick = {
                    continuePressed()
                },
                fontSize = 16.sp,
            )
            JarSecondaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_not_now),
                onClick = {
                    notNowPressed()
                },
            )
            Spacer(
                modifier = Modifier
                    .height(30.dp)
                    .fillMaxWidth()
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 1.dp)
                .background(colorResource(id = R.color.color_2e2942))
                .height(50.dp)
                .zIndex(10f)
                .align(Alignment.BottomStart),
            verticalAlignment = Alignment.Bottom
        ) {

        }
    }
}

@Composable
@Preview
fun RenderBulletRowPreview() {
    RenderBulletRow("lorem ipsum lorem lorem ipsum lorem ipsum")
}

@Composable
fun RenderBulletRow(it: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Image(
            painterResource(id = com.jar.app.feature_gold_redemption.R.drawable.feature_gold_redemption_star),
            contentDescription = "",
            modifier = Modifier
                .size(16.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            it,
            color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2),
            style = JarTypography.body1,
            lineHeight = 24.sp
        )
    }
}
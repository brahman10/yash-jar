package com.jar.app.core_compose_ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.theme.JarTypography

@Composable
fun RenderTimelineView(
    modifier: Modifier = Modifier,
    size: Int,
    RenderLeftIconContent: @Composable (index: Int) -> Unit,
    RenderRightContent: @Composable (index: Int) -> Unit,
    colorForDivider: @Composable (index: Int) -> Color,
    bottomText: String? = null,
    dividierWidth: Dp = 1.dp
) {
    Column(modifier.fillMaxWidth()) {
        for (i in 0 until size) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Column(
                    Modifier
                        .fillMaxHeight()
                        .padding(end = 10.dp), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RenderLeftIconContent(i)
                    if (i != size - 1)
                        Spacer(
                            modifier = Modifier
                                .width(dividierWidth)
                                .fillMaxHeight()
                                .background(
                                    colorForDivider(i)
                                )
                        )
                }
                RenderRightContent(i)
            }
        }
        bottomText?.takeIf { !it.isNullOrEmpty() }?.let {
            RenderPaymentBottomText(it)
        }
    }
}

@Composable
@Preview
fun RenderPaymentBottomTextPreview() {
    RenderPaymentBottomText("The amount will be refunded to your source account within 48 hours.")
}

@Composable
fun RenderPaymentBottomText(it: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 40.dp)) {
        Column (Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.fillMaxWidth().height(8.dp))
            Divider(color = colorResource(id = com.jar.app.core_ui.R.color.color_3C3357))
            Spacer(modifier = Modifier.fillMaxWidth().height(8.dp))
            Text(
                text = it,
                color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
                style = JarTypography.body1.copy(fontSize = 12.sp),
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.fillMaxWidth().height(16.dp))
        }
    }
}
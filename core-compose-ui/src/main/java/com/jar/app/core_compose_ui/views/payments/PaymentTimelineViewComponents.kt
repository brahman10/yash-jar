package com.jar.app.core_compose_ui.views.payments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.views.TransparentPillButton

import com.jar.app.core_ui.R

@Composable
internal fun RenderLeftIconContent(
    timelineViewData: TimelineViewData,
    strokedBorderVisible: Boolean = true
) {
    val stroke = Stroke(
        width = 2f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    )

    Box(Modifier
        .size(24.dp)
        .then(if (strokedBorderVisible) {
            Modifier
                .drawBehind {
                    drawCircle(
                        color = Color.White,
                        radius = 12.dp.toPx(),
                        style = stroke
                    )
                }
        } else Modifier),
        contentAlignment = Alignment.Center) {

        Icon(
            painter = painterResource(id = getIconForStatus(timelineViewData.status)),
            contentDescription = "",
            tint = Color.Unspecified,
            modifier = Modifier
                .then(
                    if (strokedBorderVisible) Modifier
                        .size(20.dp)
                    else Modifier.fillMaxSize()
                )

        )
    }
}

@Composable
internal fun RenderRightContent(
    timelineViewData: TimelineViewData,
) {
    val context = LocalContext.current
    val getStringFunction: ((Int) -> String) = {
        context.getString(it)
    }
    Row(Modifier.fillMaxWidth()) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 0.dp)
        ) {
            Row(
                Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = timelineViewData.title,
                    style = JarTypography.body2,
                    color = colorResource(id = colorForTitle(timelineViewData.status)),
                    modifier = Modifier
                        .padding(top = 0.dp, bottom = 0.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.weight(1f))
                timelineViewData.status?.let {
                    TransparentPillButton(
                        text = getTextForStatus(timelineViewData.status, getStringFunction),
                        color = colorForButtonText(timelineViewData.status),
                        onClick = {}
                    )
                }
            }
            timelineViewData.date?.let {
                Row(
                    Modifier
                        .padding(bottom = 2.dp, top = 4.dp),
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = it,
                        style = JarTypography.body2.copy(fontSize = 10.sp),
                        color = colorResource(id = R.color.color_ACA1D3),
                    )
                }
            }
        }
    }
}

@Composable
internal fun RenderRetryRow(
    refreshText: String? = null,
    shouldShowRetryButton: Boolean = false,
    shouldShowDivider: Boolean,
    refreshTextTypography: TextStyle,
    refreshTextMaxLines: Int,
    retryButtonPressed: (() -> Unit)?,
) {
    if (shouldShowDivider) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )
        Divider(
            color = colorResource(id = R.color.color_3C3357),
            thickness = 1.dp
        )
    }
    refreshText?.let {
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(10.dp)
        )
        Text(
            text = refreshText.orEmpty(),
            style = refreshTextTypography,
            maxLines = refreshTextMaxLines
        )
    }
    if (shouldShowRetryButton) {
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(10.dp)
        )
        JarPrimaryButton(
            modifier = Modifier
                .width(110.dp),
            text = stringResource(R.string.retry),
            onClick = {
                retryButtonPressed?.invoke()
            },
            isAllCaps = false,
            fontSize = 16.sp
        )
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(12.dp)
        )
    }
}

@Composable
@Preview
fun RenderLeftIconContentPreview() {
    RenderLeftIconContent(
        TimelineViewData(
            TransactionStatus.SUCCESS,
            "AsD"
        ), false
    )
}

@Composable
@Preview
fun RenderLeftIconContentPreview2() {
    RenderLeftIconContent(
        TimelineViewData(
            TransactionStatus.FAILED,
            "AsD"
        ), true
    )
}
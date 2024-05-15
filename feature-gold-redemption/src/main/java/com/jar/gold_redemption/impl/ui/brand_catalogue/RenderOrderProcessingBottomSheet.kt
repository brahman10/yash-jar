package com.jar.gold_redemption.impl.ui.brand_catalogue

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.views.CardArrow
import com.jar.app.core_compose_ui.views.EXPAND_ANIMATION_DURATION
import com.jar.app.core_ui.R
import com.jar.app.feature_gold_redemption.shared.data.network.model.PendingOrdersAPIResponse

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RenderOrderProcessingBottomSheetPreview() {
    RenderOrderProcessingBottomSheet(Modifier, mutableStateOf(true), null, {}, { v, x -> })
}
@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun RenderOrderProcessingBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: State<Boolean>,
    pendingOrdersLD: PendingOrdersAPIResponse?,
    expandBottomSheet: (Boolean) -> Unit,
    navigate: (orderId: String, voucherId: String) -> Unit
) {
    Column(
        modifier
            .background(
                colorResource(id = com.jar.app.core_ui.R.color.color_2E2942),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
    ) {
        TitleWithImage(sheetState, pendingOrdersLD?.title.orEmpty(), pendingOrdersLD?.desc.orEmpty(), expandBottomSheet)
        Spacer(Modifier.height(24.dp))
        RenderList(pendingOrdersLD, navigate)
    }
}

@Composable
internal fun RenderList(pendingOrdersLD: PendingOrdersAPIResponse?, navigate: (orderId: String, voucherId: String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxHeight(0.5f),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(pendingOrdersLD?.list.orEmpty()) {
            RenderItem(it.voucherName.orEmpty(), it.desc.orEmpty(), it.imageUrl.orEmpty(),it.orderId,it.voucherOrderId, navigate)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun RenderItem(
    title: String,
    subtitle: String,
    imageUrl: String,
    orderId: String?,
    voucherId: String?,
    navigate: (orderId: String, voucherId: String) -> Unit
) {
    Card(
        backgroundColor = colorResource(id = com.jar.app.core_ui.R.color.color_3C3357),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.debounceClickable {
            navigate(orderId.orEmpty(), voucherId.orEmpty())
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
//            Image(
//                painter = painterResource(id = R.drawable.ic_close_filled),
//                contentDescription = ""
//            )

        GlideImage(model = imageUrl, contentDescription = "", modifier = Modifier.size(32.dp))
            Column(
                Modifier
                    .padding(horizontal = 10.dp)
                    .weight(1f)
            ) {
                Text(
                    text = title,
                    style = JarTypography.h6.copy(fontSize = 14.sp),
                    color = Color.White
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = subtitle,
                    style = JarTypography.body2,
                    color = colorResource(id = R.color.color_D5CDF2)
                )
            }
            Image(
//                painter = painterResource(id = com.jar.app.feature_gold_redemption.R.drawable.feature_gold_redemption_invoice),
                painter = painterResource(id = com.jar.app.feature_gold_redemption.R.drawable.feature_gold_redemption_circle_up_right_arrow),
                contentDescription = ""
            )
        }
    }
}

@Composable
fun TitleWithImage(
    expanded: State<Boolean>,
    title: String,
    subtitle: String,
    expandBottomSheet: (Boolean) -> Unit
) {
    val transition = updateTransition(
        targetState = expanded,
        label = "Rotate"
    )
    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = EXPAND_ANIMATION_DURATION)
    }, label = "") {
        if (!it.value) 0f else 180f
    }

    Row(
        Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DashedPendingIcon()
        Column(
            Modifier
                .padding(horizontal = 10.dp)
                .weight(1f)
        ) {
            Text(text = title, style = JarTypography.h6.copy(fontSize = 16.sp), color = Color.White)
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = subtitle,
                style = JarTypography.body1,
                color = colorResource(id = R.color.color_D5CDF2)
            )
        }
        Box(
            Modifier
                .padding(0.dp)
                .size(36.dp)
                .padding(2.dp)
                .background(
                    colorResource(id = com.jar.app.core_ui.R.color.color_3C3357),
                    shape = CircleShape
                )
        ) {
            CardArrow(
                degrees = arrowRotationDegree,
                onClick = { expandBottomSheet(!expanded.value) },
                tintColor = colorResource(
                    id = R.color.color_ACA1D3
                )
            )
        }
    }
}

@Composable
@Preview
fun DashedPendingIcon() {
    val stroke = Stroke(
        width = 2f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    )
    Box(
        Modifier
            .size(36.dp)
            .drawBehind {
                drawCircle(color = Color.White, radius = 38.dp.value, style = stroke)
            }, contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = com.jar.app.feature_gold_redemption.R.drawable.feature_gold_redemption_hourglass),
            contentDescription = "",
            tint = Color.Unspecified,
            modifier = Modifier
                .size(26.dp)
                .padding(2.dp)

        )
    }
}

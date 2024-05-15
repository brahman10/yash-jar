package com.jar.gold_redemption.impl.ui.my_vouchers

import androidx.annotation.ColorRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jar.app.core_compose_ui.component.RenderImagePillButton
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.feature_gold_redemption.R
import com.jar.app.feature_gold_redemption.shared.domain.model.CardStatus


@Preview
@Composable
fun RenderStatusButton1() {
    RenderStatusButton(CardStatus.PROCESSING)
}
@Preview
@Composable
fun RenderStatusButton2() {
    RenderStatusButton(CardStatus.ACTIVE)
}
@Preview
@Composable
fun RenderStatusButton3() {
    RenderStatusButton(CardStatus.EXPIRED)
}

@Preview
@Composable
fun RenderStatusButton4() {
    RenderStatusButton(CardStatus.FAILED)
}

@ColorRes
fun getColorForOrderStatus(active: CardStatus): Int {
    return when (active) {
        CardStatus.PROCESSING -> com.jar.app.core_ui.R.color.color_EBB46A
        CardStatus.ACTIVE -> com.jar.app.core_ui.R.color.color_1EA787
        CardStatus.EXPIRED -> com.jar.app.core_ui.R.color.color_D5CDF2
        CardStatus.FAILED -> com.jar.app.core_ui.R.color.color_EB6A6E
    }
}

@Composable
fun RenderStatusButton(active: CardStatus) {
    val drawable = when (active) {
        CardStatus.PROCESSING ->  R.drawable.feature_gold_redemption_checkmark_circle
        CardStatus.ACTIVE -> R.drawable.feature_gold_redemption_icon_check_filled
        CardStatus.EXPIRED ->  R.drawable.feature_gold_redemption_error
        CardStatus.FAILED -> R.drawable.feature_gold_redemption_icon_cross_filled
    }
    val text = when (active) {
        CardStatus.PROCESSING -> "In Progress"
        CardStatus.ACTIVE -> "Active"
        CardStatus.EXPIRED -> "Expired"
        CardStatus.FAILED -> "Failed"
    }
    val bgColor = when (active) {
        CardStatus.ACTIVE -> R.color.color_1A1EA787
        CardStatus.PROCESSING -> R.color.color_1AEBB46A
        CardStatus.EXPIRED -> R.color.color_1AD5CDF2
        CardStatus.FAILED -> R.color.color_1AEB6A6E
    }
    val textColor = getColorForOrderStatus(active)
    RenderImagePillButton(
        Modifier,
        drawable,
        text,
        bgColor = bgColor,
        textColor = textColor,
        cornerRadius = 10.dp,
        style = JarTypography.h6,
        smallerTextPadding = true,
        maxLines = 1
    )
}


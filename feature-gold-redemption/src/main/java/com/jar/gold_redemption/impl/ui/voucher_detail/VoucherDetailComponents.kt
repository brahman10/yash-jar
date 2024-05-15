package com.jar.gold_redemption.impl.ui.voucher_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.base.util.copyToClipboard
import com.jar.app.core_compose_ui.component.RenderImagePillButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.views.RenderBaseToolBar
import com.jar.app.feature_gold_redemption.R
import com.jar.app.feature_gold_redemption.shared.domain.model.CardStatus
import com.jar.app.feature_gold_redemption.shared.data.network.model.RefundDetails


@Composable
@Preview
fun RenderTopBarPreview() {
    RenderTopBar(
        validTillText = "Valid ACTIVE",
        cardStatus = CardStatus.ACTIVE
    ) {
    }
}

@Composable
@Preview
fun RenderTopBarPreview2() {
    RenderTopBar(
        validTillText = "Valid EXPIRED",
        cardStatus = CardStatus.EXPIRED
    ) {
    }
}

@Composable
@Preview
fun RenderTopBarPreview422() {
    RenderTopBar(
        validTillText = "Valid FAILED",
        cardStatus = CardStatus.FAILED
    ) {
    }
}

@Composable
@Preview
fun RenderTopBarPreview42() {
    RenderTopBar(
        validTillText = "Valid PROCESSING",
        cardStatus = CardStatus.PROCESSING
    ) {
    }
}
@Composable
@Preview
fun RenderTopBarPreview423() {
    RenderTopBar(
        validTillText = "",
        refundDetails = RefundDetails("", "", "", ""),
        cardStatus = CardStatus.PROCESSING
    ) {
    }
}



@Composable
@Preview
fun VoucherPinContainerPreview() {
    VoucherPinContainer("123-123")
}

@Composable
fun VoucherPinContainer(pin: String?) {
    val context = LocalContext.current
    val copiedToClipboard = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.featuer_gold_redemption_copied_to_clipboard)
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .background(
                color = colorResource(id = com.jar.app.core_ui.R.color.color_3C3357),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_voucher_pin),
            color = Color(0xffd5cdf2),
            lineHeight = 21.sp,
            style = TextStyle(
                fontSize = 18.sp
            ),
            modifier = Modifier
                .padding(vertical = 14.dp)
        )
        Spacer(
            modifier = Modifier
                .weight(1f)
        )
            Text(
                text = pin.orEmpty(),
                color = Color.White,
                style = com.jar.app.core_compose_ui.theme.JarTypography.h6,
                fontSize = 20.sp,
                letterSpacing = 0.3.sp
            )
            Spacer(
                modifier = Modifier
                    .width(width = 8.dp)
            )
            Image(
                painter = painterResource(id = com.jar.app.core_ui.R.drawable.ic_copy),
                contentDescription = "Subtract",
                modifier = Modifier
                    .debounceClickable {
                        context.copyToClipboard(pin.orEmpty(), copiedToClipboard)
                    }
                    .padding(4.dp)
            )
        }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TitleWithImage(imageUrl: String?, voucherName: String?) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(colorResource(id = com.jar.app.core_ui.R.color.color_2E2942))
            .padding(start = 16.dp, top = 24.dp, bottom = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(model = imageUrl, contentDescription = "", modifier = Modifier.size(50.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = voucherName.orEmpty(),
            style = JarTypography.h6,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.Top)
                .padding(start = 10.dp),
            fontSize = 24.sp
        )
    }
}

@Composable
fun RenderTopBar(
    validTillText: String?,
    cardStatus: CardStatus = CardStatus.ACTIVE,
    refundDetails: RefundDetails? = null,
    rightButtonClick: () -> Unit
) {
    if (refundDetails != null) {
        RenderTopBarHelper(
            bgColor = com.jar.app.feature_gold_redemption.R.color.color_1EA787_20,
            drawableIcon = com.jar.app.feature_gold_redemption.R.drawable.feature_gold_redemption_icon_check_filled,
            textFinal = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_refund_initiated),
            textColor = com.jar.app.core_ui.R.color.color_1EA787,
            buttonBgColor = com.jar.app.feature_gold_redemption.R.color.color_273442,
            buttonTextColor = com.jar.app.core_ui.R.color.white,
            cardStatus = cardStatus,
            validTillText = validTillText,
            rightButtonClick
        )
    } else {
        val bgColor =
            when (cardStatus) {
                CardStatus.ACTIVE -> com.jar.app.core_ui.R.color.color_1EA787
                CardStatus.PROCESSING -> com.jar.app.core_ui.R.color.color_43353B
                CardStatus.EXPIRED -> com.jar.app.core_ui.R.color.color_EB6A6E
                CardStatus.FAILED -> com.jar.app.feature_gold_redemption.R.color.color_EB6A6E_20
            }
        val drawableIcon =
            when (cardStatus) {
                CardStatus.PROCESSING -> R.drawable.feature_gold_redemption_hourglass_icon
                CardStatus.ACTIVE -> R.drawable.feature_gold_redemption_checkmark_circle_green_tick
                CardStatus.EXPIRED -> R.drawable.feature_gold_redemption_error
                CardStatus.FAILED -> R.drawable.feature_gold_redemption_icon_cross_filled
            }

        val textFinal: String? =
            when (cardStatus) {
                CardStatus.PROCESSING -> stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_processing)
                CardStatus.ACTIVE -> null
                CardStatus.EXPIRED -> null
                CardStatus.FAILED -> stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_failed)
            }
        val textColor =
            when (cardStatus) {
                CardStatus.PROCESSING -> com.jar.app.core_ui.R.color.color_EBB46A
                CardStatus.ACTIVE -> com.jar.app.core_ui.R.color.white
                CardStatus.EXPIRED -> com.jar.app.core_ui.R.color.white
                CardStatus.FAILED -> com.jar.app.core_ui.R.color.color_EB6A6E
            }
        val buttonBgColor =
            when (cardStatus) {
                CardStatus.PROCESSING -> com.jar.app.core_ui.R.color.color_32282C
                CardStatus.ACTIVE -> com.jar.app.core_ui.R.color.white
                CardStatus.EXPIRED -> com.jar.app.core_ui.R.color.white
                CardStatus.FAILED -> com.jar.app.feature_gold_redemption.R.color.color_491F20
            }
        val buttonTextColor =
            when (cardStatus) {
                CardStatus.PROCESSING -> com.jar.app.core_ui.R.color.color_EBB46A
                CardStatus.ACTIVE -> com.jar.app.core_ui.R.color.white
                CardStatus.EXPIRED -> com.jar.app.core_ui.R.color.white
                CardStatus.FAILED -> com.jar.app.core_ui.R.color.white
            }
        RenderTopBarHelper(
            bgColor,
            drawableIcon,
            textFinal,
            textColor,
            buttonBgColor,
            buttonTextColor,
            cardStatus,
            validTillText,
            rightButtonClick
        )
    }
}

@Composable
fun RenderTopBarHelper(
    bgColor: Int,
    drawableIcon: Int,
    textFinal: String?,
    textColor: Int,
    buttonBgColor: Int,
    buttonTextColor: Int,
    cardStatus: CardStatus,
    validTillText: String?,
    rightButtonClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(if (cardStatus in setOf(CardStatus.PROCESSING, CardStatus.FAILED)) 56.dp else 36.dp)
            .background(colorResource(id = bgColor)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (cardStatus in setOf(
                CardStatus.PROCESSING,
                CardStatus.FAILED
            )
        ) Arrangement.Start else Arrangement.Center
    ) {
        if (cardStatus in setOf(CardStatus.PROCESSING, CardStatus.FAILED)) {
            Spacer(
                modifier = Modifier
                    .width(16.dp)
            )
        }
        Image(
            painter = painterResource(id = drawableIcon),
            contentDescription = "",
            colorFilter = generateColorFilter(cardStatus)
        )
        Spacer(modifier = Modifier.width(8.0.dp))
        Text(
            text = validTillText ?: textFinal.orEmpty(),
            color = colorResource(id = textColor),
            style = JarTypography.h6.copy(fontWeight = FontWeight.SemiBold),
        )
        if (cardStatus in setOf(CardStatus.PROCESSING, CardStatus.FAILED)) {
            Spacer(modifier = Modifier.weight(1f))
            RenderImagePillButton(
                modifier = Modifier.heightIn(min = 40.dp).clickable {
                    rightButtonClick()
                },
                text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_see_details),
                bgColor = buttonBgColor,
                textColor = buttonTextColor,
                cornerRadius = 8.dp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.width(16.0.dp))
        }
    }
}

@Composable
fun generateColorFilter(cardStatus: CardStatus): ColorFilter? {
    return if (cardStatus == CardStatus.EXPIRED) ColorFilter.tint(color = colorResource(id = com.jar.app.core_ui.R.color.white))
    else null
}

@Composable
fun RenderToolBar(string: String? = null, showShowShareButton: Boolean, OnRightImageClick: () -> Unit, backPress: () -> Unit) {
    RenderBaseToolBar(
        modifier = Modifier.background(colorResource(id = com.jar.app.core_ui.R.color.color_2e2942)),
        onBackClick = {
            backPress()
        },
        title = string.orEmpty(),
        {
            if (showShowShareButton)
                Image(
                    painterResource(R.drawable.feature_gold_redemption_share),
                    "",
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .debounceClickable { OnRightImageClick() }
                )
        }
    )
}

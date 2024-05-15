package com.jar.gold_redemption.impl.ui.my_vouchers.bottom_sheet

import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.widget.TextViewCompat
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.base.util.copyToClipboard
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.component.RenderImagePillButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_ui.widget.EmbossedTextView
import com.jar.app.feature_gold_redemption.R
import com.jar.app.feature_gold_redemption.shared.domain.model.CardType
import com.jar.gold_redemption.impl.ui.my_vouchers.RenderStatusButton
import com.jar.app.feature_gold_redemption.shared.domain.model.CardStatus
import com.jar.app.feature_gold_redemption.shared.data.network.model.UserVoucher
import com.jar.gold_redemption.impl.ui.my_vouchers.VoucherCardBottomBg
import com.jar.gold_redemption.impl.ui.my_vouchers.VoucherCardTopBg
import com.jar.gold_redemption.impl.ui.my_vouchers.getColorForOrderStatus
import java.lang.ref.WeakReference


@Composable
@Preview
fun PreviewRenderVoucherDetailItem() {
    RenderVoucherDetailItem(
        Modifier,
        UserVoucher(
            voucherId = "",
            amount = 1,
            imageUrl = "",
            viewDetails = "",
            voucherName = "ASDASD",
            calendarUrl = "",
            code = "asdasd",
            noOfDaysToRedeem = 24,
            voucherExpiredText = "asdasd",
            voucherProcessingText = "asdas",
            validTillText = "ASdasd",
            myVouchersType = CardStatus.ACTIVE.name,
            creationDate = "",
            quantity = 0
        ),
        showCopyClipboardBtn = false
    )
}

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

@Composable
@Preview
fun RenderFAQsButtonPreview() {
    RenderFAQsButton() {}
}
@Composable
fun RenderFAQsButton(navigate: () -> Unit) {
    Text(
        text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_faqs),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .background(
                colorResource(id = com.jar.app.core_ui.R.color.color_272239),
                shape = RoundedCornerShape(30.dp)
            )
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .debounceClickable {
                navigate()
            },
        color = colorResource(id = com.jar.app.core_ui.R.color.color_EEEAFF),
        style = JarTypography.body1
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun RenderVoucherDetailCard(
    modifier: Modifier = Modifier.fillMaxWidth(),
    amount: Float? = null,
    voucher: UserVoucher?,
    bgColor: Color = colorResource(id = com.jar.app.core_ui.R.color.color_272239),
    clickListener: ((UserVoucher?) -> Unit)? = null,
    showCardContainer: Boolean = true,
    quantity: String = "",
    showCardNoHideBtn: Boolean = true,
    showCopyClipboardBtn: Boolean = true,
    alpha: Float = 1f,
    isCardNoHidden: Boolean? = null,
    elevation: Dp = 30.dp,
    voucherCardType: CardType? = null,
    viewRef: WeakReference<View?>? = null,
    showVoucherNameOnBanner: Boolean? = null,
    copyClipboardAnalytics: (() -> Unit)? = null,
    showQuantityLabel: Boolean = true,
    imageUrl: String? = null,
) {
    val text = if (showVoucherNameOnBanner == true) voucher?.voucherName.orEmpty() else
        if ((voucherCardType ?: (voucher)?.getCardTypeEnum()) == CardType.DIAMOND)
            stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_diamond_voucher) else
            stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_gold_voucher)
    val cardType = voucherCardType ?: voucher?.getCardTypeEnum() ?: CardType.GOLD
    BoxWithConstraints(
        modifier = modifier
            .background(bgColor)
            .alpha(alpha)
    ) {
        val topPadding = 34.dp + 24.dp + 12.dp
        voucher?.noOfDaysToRedeem?.takeIf { it != null && it < 30 && it > 0 }?.let {
            RenderImagePillButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 8.dp)
                    .zIndex(10f),
                drawableRes = com.jar.app.feature_gold_redemption.R.drawable.feature_gold_redemption_time,
                text = stringResource(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_days_left, it),
                bgColor = com.jar.app.core_ui.R.color.color_4d9de0,
                textColor = com.jar.app.core_ui.R.color.white,
                cornerRadius = 12.dp,
                smallerTextPadding = true,
                maxLines = 1
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = if ((voucher?.noOfDaysToRedeem ?: 100) < 30) 12.dp else 25.dp)
                .debounceClickable {
                    clickListener?.invoke(voucher)
                },
            shape = RoundedCornerShape(12.dp),
            elevation = elevation,
            backgroundColor = bgColor
        ) {
            Column(modifier = Modifier
                .fillMaxWidth(), verticalArrangement = Arrangement.Top) {
                VoucherCardTopBg(cardType = cardType ?: voucher?.getCardTypeEnum() ?: CardType.GOLD) {
                    Column(modifier = Modifier
                        .fillMaxWidth()) {
                        val currencyAmount = stringResource(
                            id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_currency_sign_x_string,
                            (amount?.toInt() ?: voucher?.amount.orZero().toInt()).getFormattedAmount()
                        )
                        AndroidView(modifier = Modifier.padding(top = 12.dp, start = 16.dp), factory = { ctx ->
                            EmbossedTextView(ctx).apply {
                                TextViewCompat.setTextAppearance(this, com.jar.app.core_ui.R.style.CommonBoldTextViewStyle)
                                this.text = currencyAmount
                                val colorArray: IntArray = when (cardType) {
                                    CardType.GOLD, CardType.NONE, null -> this.GOLD_GRADIENT
                                    CardType.DIAMOND -> this.DIAMOND_GRADIENT
                                }
                                this.setColor(colorArray)
                                this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f)
                            }
                        }, update = {
                            it.text = currencyAmount
                            val colorArray: IntArray = when (cardType) {
                                CardType.GOLD, CardType.NONE, null -> it.GOLD_GRADIENT
                                CardType.DIAMOND -> it.DIAMOND_GRADIENT
                            }
                            it.setColor(colorArray)
                        })
                        if (showCardContainer)
                            CardNumberContainer(
                                code = voucher?.code,
                                modifier = Modifier.padding(start = 20.dp, top = 4.dp, bottom = 10.dp),
                                showCardNoHideBtn = showCardNoHideBtn,
                                showCopyClipboardBtn = showCopyClipboardBtn,
                                isCardNoHidden = isCardNoHidden,
                                viewRef = viewRef,
                                copyClipboardAnalytics = copyClipboardAnalytics
                            )
                    }
                }
                VoucherCardBottomBg(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(Modifier.width(16.dp))
                    imageUrl?.let {
                        GlideImage(
                            it,
                            contentDescription = "",
                            modifier = Modifier.padding(start = 8.dp).size(24.dp),
                        )
                    }
                    Text(
                        text = text.orEmpty(),
                        modifier = Modifier.padding(start = 8.dp),
                        color = Color.White,
                        style = JarTypography.body1
                    )
                    Spacer(Modifier.weight(1f))
                    (voucher?.quantity.toStringOrNull() ?: quantity)?.takeIf { !it.isNullOrBlank() && voucher?.getStatusEnum() != CardStatus.ACTIVE && showQuantityLabel }?.let { quantity ->
                        Text(
                            generateAmountText(voucher?.amount, quantity),
                            style = JarTypography.h6,
                            modifier = Modifier.padding(start = 8.dp),
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.width(24.dp))
                }
            }
        }
    }
}

fun generateAmountText(amount: Int?, quantity: String): String {
    if (amount == null) return "x ${quantity}"
    return "$amount x ${quantity}"
}

fun Int?.toStringOrNull(): String? {
    if (this == null) return null
    return this.toString()
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun RenderVoucherDetailItem(
    modifier: Modifier = Modifier,
    it: UserVoucher?,
    quantity: String = "",
    showCardNoHideBtn: Boolean = false,
    showCopyClipboardBtn: Boolean,
    isCardNoHidden: Boolean? = null,
    viewRef: WeakReference<View?>? = null,
    copyClipboardAnalytics: (() -> Unit)? = null,
    clickListener: ((UserVoucher?) -> Unit)? = null,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .background(color = colorResource(id = com.jar.app.core_ui.R.color.color_272239))
                .debounceClickable {
                    clickListener?.invoke(it)
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
//            GlideImage(
//                model = it?.imageUrl,
//                contentDescription = "",
//                modifier = Modifier.size(30.dp)
//            )
            Text(
                text = it?.voucherName ?: "",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
                color = Color.White,
                style = JarTypography.h6,
                fontSize = 18.sp
            )
            Icon(
                painter = painterResource(id = R.drawable.feature_gold_redemption_right_chevron_bold),
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier.padding(end = 4.dp)
            )
        }
        RenderVoucherDetailCard(
            amount = it?.amount.orZero().toFloat(),
            voucher = it,
            clickListener = clickListener,
            quantity = quantity,
            showCardNoHideBtn = showCardNoHideBtn,
            showCopyClipboardBtn = showCopyClipboardBtn,
            alpha =  if ((it?.getStatusEnum()) == CardStatus.ACTIVE) 1f else 0.5f,
            isCardNoHidden = isCardNoHidden,
            voucherCardType = null,
            viewRef = viewRef,
            copyClipboardAnalytics = copyClipboardAnalytics
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = colorResource(id = com.jar.app.core_ui.R.color.color_272239))
                .padding(top = 16.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            (it?.getStatusEnum())?.let {
                RenderStatusButton(it)
            }
            (it?.validTillText?.replace("Active ", "") ?: it?.voucherExpiredText)?.let { string ->
                Text(
                    text = string,
                    color = colorResource(id = getColorForOrderStatus(it?.getStatusEnum() ?: CardStatus.ACTIVE)),
                    style = JarTypography.body1,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            it?.refundInitText?.let {
                Text(
                    text = it,
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_58DDC8),
                    style = JarTypography.body1.copy(fontSize = 12.sp),
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    textAlign = TextAlign.End
                )
            } ?: run {
                it?.creationDate?.let {
                    Text(
                        text = it,
                        color = colorResource(id = com.jar.app.core_ui.R.color.color_776E94),
                        style = JarTypography.body1.copy(fontSize = 12.sp),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }

}

@Composable
fun CardNumberContainer(
    code: String?,
    modifier: Modifier = Modifier,
    showCardNoHideBtn: Boolean,
    showCopyClipboardBtn: Boolean,
    isCardNoHidden: Boolean? = null,
    viewRef: WeakReference<View?>? = null,
    copyClipboardAnalytics: (() -> Unit)?
) {
    val isHidden = remember { mutableStateOf<Boolean>(isCardNoHidden ?: !showCardNoHideBtn) }
    val context = LocalContext.current
    val copiedToClipboard = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.featuer_gold_redemption_copied_to_clipboard)

    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = if (isHidden.value) stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_hidden_card_no) else code
               .orEmpty(), color = Color.White, style = JarTypography.body1.copy(fontSize = 18.sp)
        )
        if (showCopyClipboardBtn)
            Image(
                painterResource(R.drawable.clipboard),
                "",
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .debounceClickable {
                        copyClipboardAnalytics?.invoke()
                        context.copyToClipboard(code.orEmpty())
                        viewRef
                            ?.get()
                            ?.let {
                                Toast
                                    .makeText(it.context, copiedToClipboard, Toast.LENGTH_SHORT)
                                    .show()
                            }
                    })
        if (showCardNoHideBtn) {
            RenderImagePillButton(
                modifier = Modifier.debounceClickable {
                    isHidden.value = !isHidden.value
                },
                text = if (!isHidden.value) stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_hide) else stringResource(
                    id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_show
                ),
                bgColor = com.jar.app.core_ui.R.color.color_A6AAAA,
                textColor = com.jar.app.core_ui.R.color.white,
                maxLines = 1
            )
        }
    }
}

@Composable
@Preview
fun CardNumberContainerPreview() {
    CardNumberContainer(
        "3424 1523 2523 3212",
        showCardNoHideBtn = true,
        showCopyClipboardBtn = false,
        viewRef = null,
        copyClipboardAnalytics = { }
    )
}



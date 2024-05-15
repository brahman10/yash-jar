package com.jar.gold_redemption.impl.ui.voucher_purchase

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.utils.generateAnnotatedFromHtmlString
import com.jar.app.core_compose_ui.utils.toAnnotatedString
import com.jar.app.core_compose_ui.views.RenderBaseToolBar
import com.jar.app.core_utils.data.getSpannable
import com.jar.app.feature_gold_redemption.R
import com.jar.gold_redemption.impl.ui.common_ui.AboutJewellerContainer
import com.jar.app.feature_gold_redemption.shared.data.network.model.VoucherStaticContent


@Composable
fun RenderCartContainer(amount1: BoxScope, amount: Float, proceed: () -> Unit) {
    amount1?.apply {
        Row(
            Modifier
                .fillMaxWidth()
                .background(colorResource(id = com.jar.app.core_ui.R.color.color_272239))
                .height(100.dp)
                .align(Alignment.BottomCenter)
                .padding(top = 16.dp, start = 24.dp, end = 24.dp)
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    stringResource(
                        id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_total_amount,
                    ),
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2),
                    style = JarTypography.body1,
                    modifier = Modifier.padding()
                )
                Text(
                    stringResource(
                        id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_currency_sign_x_string,
                        amount.getFormattedAmount()
                    ),
                    color = colorResource(id = com.jar.app.core_ui.R.color.white),
                    style = JarTypography.h6,
                    modifier = Modifier.padding(top = 4.dp),
                    fontSize = 26.sp
                )
            }
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                JarPrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_proceed),
                    onClick = { proceed() },
                    fontSize = 16.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable()
fun VoucherHeader(title: String, imageUrl: String, goldBannerText: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.0.dp),
    ) {
            GlideImage(
                imageUrl,
                "",
                Modifier
                    .clip(RoundedCornerShape(12.0.dp))
                    .size(100.0.dp, 100.0.dp)
                    .background(Color(1.0f, 1.0f, 1.0f, 1.0f))
            )

        Column(
            Modifier
                .fillMaxWidth()
                .height(100.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                title,
                Modifier
                    .fillMaxWidth(),
                color = Color(0.93f, 0.92f, 1.0f, 1.0f),
                style = JarTypography.h5,
                fontSize = 20.sp,
                lineHeight = 24.sp
            )

            Row(
                Modifier
                    .background(Color(0.12f, 0.65f, 0.53f, 0.15f), RoundedCornerShape(8.0.dp)),
                horizontalArrangement = Arrangement.spacedBy(4.0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(8.0.dp))
                Text(
                    goldBannerText.generateAnnotatedFromHtmlString(),
                    Modifier
                        .wrapContentHeight(Alignment.Top)
                        .padding(vertical = 6.dp),
                    color = Color(0.35f, 0.87f, 0.78f, 1.0f),
                    style = JarTypography.body1.copy(fontSize = 12.sp)
                )
                Spacer(modifier = Modifier.width(8.0.dp))
            }
        }
    }
}


@Composable()
@Preview
fun AmountQuantityContainerPreview() {
    val amountList = remember { mutableStateOf<Float>(500f) }
    val amountFloat = remember { mutableStateOf<List<Float>>(listOf(500f)) }

    AmountQuantityContainer(
        amountList,
        true,
        true, 1, {}, {}, {}
    ) {}
}

@Composable()
fun AmountQuantityContainer(
    selectedAmount: State<Float?>,
    isMinusEnabled: Boolean,
    isPlusEnabled: Boolean,
    quantity: Int,
    onMinusClick: () -> Unit,
    onAddClick: () -> Unit,
    hiddenClick: () -> Unit,
    onSelectAmountClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Column(
            Modifier
                .fillMaxWidth(0.45f),
            verticalArrangement = Arrangement.spacedBy(8.0.dp),
            horizontalAlignment = Alignment.Start
        ) {

            Text(
                stringResource(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_select_amount),
                Modifier
                    .wrapContentHeight(Alignment.Top)
                    .size(148.0.dp, 21.0.dp)
                    .fillMaxWidth()
                    .clickable {
                        onSelectAmountClick()
                    },
                color = Color(0.67f, 0.63f, 0.83f, 1.0f),
                style = JarTypography.body2
            )

            Row(
                Modifier
                    .clip(RoundedCornerShape(8.0.dp))
                    .height(52.0.dp)
                    .background(colorResource(id = com.jar.app.core_ui.R.color.color_38334c))
                    .fillMaxWidth()
                    .clickable {
                        onSelectAmountClick()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(16.0.dp))
                Text(
                    stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_amount_s, selectedAmount.value.orZero().toInt()),
                    Modifier
                        .wrapContentHeight(Alignment.Top),
                    color = Color(1.0f, 1.0f, 1.0f, 1.0f),
                    style = JarTypography.h6,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.weight(1.0f))
                Icon(
                    Icons.Filled.ArrowDropDown,
                    "Trailing icon for exposed dropdown menu",
                    tint = colorResource(id = com.jar.app.core_ui.R.color.white)
                )
                Spacer(modifier = Modifier.width(16.0.dp))
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth(0.2f)
        )

        Column(
            Modifier.fillMaxWidth(1f),
        ) {
            Text(
                stringResource(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_select_quantity),
                Modifier
                    .fillMaxWidth(),
                color = Color(0.67f, 0.63f, 0.83f, 1.0f),
                style = JarTypography.body2
            )
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(8.0.dp))

            Row(
                Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(52.0.dp, 52.0.dp)
                        .background(
                            colorResource(id = com.jar.app.core_ui.R.color.color_38334c),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .alpha(if (isMinusEnabled) 1f else 0.3f)
                        .align(Alignment.CenterVertically)
                        .debounceClickable(debounceInterval = 200L) {
                            if (isMinusEnabled)
                                onMinusClick()
                            else hiddenClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "-",
                        style = JarTypography.body1,
                        color = Color.White,
                        fontSize = 24.sp
                    )
                }
                Spacer(modifier = Modifier
                    .weight(1f))
                Text(
                    quantity.toString(),
                    Modifier
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 8.dp),
                    color = Color(1.0f, 1.0f, 1.0f, 1.0f),
                    textAlign = TextAlign.Center,
                    style = JarTypography.h6
                )
                Spacer(modifier = Modifier
                    .weight(1f))
                Box(
                    Modifier
                        .size(52.0.dp, 52.0.dp)
                        .background(
                            colorResource(id = com.jar.app.core_ui.R.color.color_38334c),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .alpha(if (isPlusEnabled) 1f else 0.3f)
                        .align(Alignment.CenterVertically)
                        .debounceClickable(debounceInterval = 200L) {
                            if (isPlusEnabled)
                                onAddClick()
                            else hiddenClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        style = JarTypography.body1,
                        color = Color.White,
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RenderToolBar(OnRightImageClick: () -> Unit, backPress: () -> Unit) {
    RenderBaseToolBar(
        modifier = Modifier.background(colorResource(id = com.jar.app.core_ui.R.color.color_2E2942)),
        onBackClick = {
            backPress()
        },
        title = " ",
        colorFilter = ColorFilter.tint(colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2)),
        RightSection = {
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

@Composable
@Preview
fun AboutJewellerContainerPreview() {
    AboutJewellerContainer(Modifier, onStatesDropDownClick = {},  analyticsFunction = {})
}


@Composable()
@Preview
fun ImageInfoContainerPreview() {
    val list = listOf<VoucherStaticContent>(
        VoucherStaticContent(
            "https://d21tpkh2l1zb46.cloudfront.net/Images/Gold_Redemption/cart.png",
            "Redeem [online]",
        ),
        VoucherStaticContent(
            "https://d21tpkh2l1zb46.cloudfront.net/Images/Gold_Redemption/calendar.png",
            "No returns or\n refunds",
        ),
        VoucherStaticContent(
            "https://d21tpkh2l1zb46.cloudfront.net/Images/Gold_Redemption/cancel.png",
            "Valid for \n6 months",
        ),
        VoucherStaticContent(
            "https://d21tpkh2l1zb46.cloudfront.net/Images/Gold_Redemption/offer.png",
            "Combine voucher with\n store offers"
        ),
    )
    ImageInfoContainer(list)
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable()
private fun RowScope.RoundImageWithText(imageUrl: String, text: String) {
    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
        GlideImage(
            imageUrl,
            contentDescription = "",
            modifier = Modifier
                .size(52.dp)
                .background(
                    colorResource(id = com.jar.app.core_ui.R.color.color_272239),
                    shape = CircleShape
                )
                .padding(6.dp)
        )
        Text(
            modifier = Modifier
                .padding(top = 8.dp, start = 8.dp, end = 8.dp),
            text = text,
            color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2),
            style = JarTypography.body1,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
fun OnlineRedemptionContainer(inStoreRedemptionText: String?, onlineRedemptionText: String?) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(colorResource(id = com.jar.app.core_ui.R.color.color_272239))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
        )
        onlineRedemptionText?.let {
            Text(
                stringResource(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_online_redemption).uppercase(),
                Modifier,
                color = Color(0.67f, 0.63f, 0.83f, 1.0f),
                style = JarTypography.h6
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
            )
        }
        inStoreRedemptionText?.let {
            Text(
                "Please visit <u>www.candere.com</u>  to redeem this voucher.".getSpannable()
                    .toAnnotatedString(),
                Modifier,
                color = Color(0.93f, 0.92f, 1.0f, 1.0f),
                style = JarTypography.body1
            )
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
        )
    }
}

@Composable()
internal fun ImageInfoContainer(voucherStaticContentList: List<VoucherStaticContent?>?) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(colorResource(id = com.jar.app.core_ui.R.color.color_121127))
            .padding(horizontal = 10.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 32.dp), Arrangement.SpaceAround
        ) {
            for (i in 0 until 2) {
                voucherStaticContentList?.getOrNull(i)?.let {
                    this.RoundImageWithText(
                        it.imageUrl.orEmpty(),
                        it.title.orEmpty()
                    )
                }
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, top = 24.dp), Arrangement.SpaceAround
        ) {
            for (i in 2 until 4) {
                voucherStaticContentList?.getOrNull(i)?.let {
                    this.RoundImageWithText(
                        it.imageUrl.orEmpty(),
                        it.title.orEmpty()
                    )
                }
            }
        }
    }
}
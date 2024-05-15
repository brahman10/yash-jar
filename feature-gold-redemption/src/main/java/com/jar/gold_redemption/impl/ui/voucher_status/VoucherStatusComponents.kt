package com.jar.gold_redemption.impl.ui.voucher_status

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.base.util.copyToClipboard
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_ui.R
import com.jar.gold_redemption.impl.ui.my_vouchers.RenderPurchaseHistoryItem
import com.jar.app.feature_gold_redemption.shared.data.network.model.PurchaseItemData
import com.jar.app.feature_gold_redemption.shared.data.network.model.GoldRedemptionTransactionData


@Composable
fun RenderToolBar(
    headingText: State<String?>,
    subtitle: String,
    forBonus: Boolean = false,
    backPress: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(colorResource(id = com.jar.app.core_ui.R.color.color_272239)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(R.drawable.ic_arrow_back),
            "BackArrow",
            modifier = Modifier
                .padding(start = 18.dp)
                .debounceClickable { backPress() },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        ) {
            Text(
                headingText.value.orEmpty(),
                color = Color.White,
                style = JarTypography.h6,
                fontSize = 16.sp
            )
            Text(
                text = subtitle,
                color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
                style = JarTypography.body1
            )
        }
    }
}


@Composable
internal fun VoucherRow(data: GoldRedemptionTransactionData?, forBonus: Boolean) {
    VoucherRow(
        if (forBonus)data?.voucherBonusImageUrl.orEmpty() else data?.imageUrl.orEmpty(),
        if (forBonus) stringResource(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_voucher_bonus) else data?.voucherTypeString.orEmpty(),
        data?.brandName.orEmpty(),
        if (forBonus) data?.voucherBonusAmountString.orEmpty() else data?.amount.orEmpty(),
        if (!forBonus) data?.quantityMultipliedAmountString.orEmpty() else "",
    )
}


@Composable
internal fun AddedForRow(
    purchaseItemData: PurchaseItemData
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
    ) {
        Text(
            text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_added_for),
            color = colorResource(id = com.jar.app.core_ui.R.color.color_EEEAFF),
            fontSize = 20.sp,
            style = JarTypography.h6,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        RenderPurchaseHistoryItem(
            purchaseItem = purchaseItemData
        ) { s1 ->
            val string = ""
        }
    }
}

@Composable
fun OrderIdRow(it: String) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 0.dp),
        elevation = 0.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = colorResource(id = com.jar.app.core_ui.R.color.color_2E2942)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_order_id),
                    color = colorResource(
                        id = com.jar.app.core_ui.R.color.color_ACA1D3
                    ),
                    style = JarTypography.body1
                )
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        it,
                        color = colorResource(
                            id = com.jar.app.core_ui.R.color.color_EEEAFF
                        ),
                        style = JarTypography.body1
                    )
                    Icon(
                        painter = painterResource(id = com.jar.app.core_ui.R.drawable.ic_copy),
                        contentDescription = stringResource(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_copy),
                        tint = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
                        modifier = Modifier
                            .padding(start = 8.dp, top = 8.dp)
                            .size(16.dp)
                            .clickable {
                                context.copyToClipboard(it)
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun PaidForRow(it: String, onPaidRowPress: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp)
            .clickable { onPaidRowPress() },
        elevation = 0.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = colorResource(id = com.jar.app.core_ui.R.color.color_2E2942)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)) {
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_paid_for), color = colorResource(
                    id = com.jar.app.core_ui.R.color.color_ACA1D3)
                )
                Text(it, style = JarTypography.body1, color = Color.White, modifier = Modifier.padding(top = 4.dp))
            }
            Icon(
                painterResource(id = R.drawable.ic_arrow_forward),
                "ArrowForward",
                tint = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
                modifier = Modifier
                    .size(30.dp)
                    .padding(end = 16.dp)
                    .align(Alignment.CenterVertically)
            )
        }

    }
}
@Composable
@Preview
fun xxx() {
    VoucherRow("https://www.google.com", "title", "subtitle", "rightAmount", "rightAmountSubtitle")
}
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun VoucherRow(imageUrl: String, title: String, subtitle: String, rightAmount: String, rightAmountSubtitle: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(colorResource(id = com.jar.app.core_ui.R.color.color_272239))
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
//        Image(painter = painterResource(id = R.drawable.ic_close_filled), contentDescription = "")
        GlideImage(model = imageUrl, contentDescription = "", modifier = Modifier
            .size(32.dp))
        Column (
            Modifier
                .padding(horizontal = 10.dp)
                .weight(1f)) {
            Text(text = title, style = JarTypography.h6, color = Color.White)
            Text(modifier = Modifier.padding(top = 4.dp), text = subtitle, style = JarTypography.body2, color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3))
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = generateAmount(rightAmount), style = JarTypography.h5, color = Color.White)
            Text(modifier = Modifier.padding(top = 4.dp), text = rightAmountSubtitle, style = JarTypography.body2, color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3))
        }
    }
}
@Composable
fun generateAmount(rightAmount: String): String {
    return rightAmount.toIntOrNull()?.let {
        stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_feature_buy_gold_currency_sign_x_string, it.getFormattedAmount())
    } ?: rightAmount
}

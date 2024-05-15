package com.jar.gold_redemption.impl.ui.voucher_success

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_ui.R
import com.jar.app.feature_gold_redemption.shared.data.network.model.PurchaseItemData

@Composable
@Preview
fun VoucherPurchaseSummaryItemPreview() {
    VoucherPurchaseSummaryItem(
        purchaseItem = PurchaseItemData(
            amount = "100",
            voucherId = "123",
            amountQuantityString = "100g",
            date = "2021-09-09",
            desc = "desc",
            imageUrl = "https://www.google.com",
            quantity = 1,
            txnStatus = "status",
            title = "title",
            bottomDrawerObjectType = "asd",
            dateString = "qwe",
            voucherOrderStatus = null,
            goldBonusTxnStatus = null,
            refundStatus = null
        )
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun VoucherPurchaseSummaryItem(purchaseItem: PurchaseItemData) {
    Card(
        Modifier
            .fillMaxWidth()
            .clickable { purchaseItem?.voucherId?.let {  } },
        shape = RoundedCornerShape(16.dp),
        backgroundColor = colorResource(
            id = R.color.color_2e2942
        ),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideImage(
                model = purchaseItem?.imageUrl.orEmpty(),
                contentDescription = "",
                modifier = Modifier
                    .size(40.dp)
            )
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 16.dp)) {
                Row {
                    Text(
                        text = purchaseItem?.title.orEmpty(),
                        style = JarTypography.h6,
                        maxLines = 1,
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = purchaseItem?.amount?: "",
                        style = JarTypography.h6,
                        maxLines = 1,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
                Row {
                    Text(
                        text = "${purchaseItem?.desc}".orEmpty(),
                        style = JarTypography.body1,
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.color_ACA1D3),
                        modifier = Modifier
                            .padding(top = 4.dp, end = 6.dp)
                            .weight(1f),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}


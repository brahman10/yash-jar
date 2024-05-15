package com.jar.gold_redemption.impl.ui.my_vouchers

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
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_BONUS
import com.jar.app.feature_gold_redemption.shared.domain.model.GoldRedemptionManualPaymentStatus
import com.jar.app.feature_gold_redemption.shared.data.network.model.PurchaseItemData
import com.jar.app.feature_gold_redemption.shared.domain.model.RefundStatus
import com.jar.app.feature_gold_redemption.shared.util.curateLoadingStatus
import com.jar.app.feature_gold_redemption.shared.util.getGoldRedemptionStatusFromString

@Composable
@Preview
fun RenderPurchaseHistoryItemPreview() {
    RenderPurchaseHistoryItem(null) { it ->

    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun RenderPurchaseHistoryItem(
    purchaseItem: PurchaseItemData?,
    navigate: ((PurchaseItemData) -> Unit)? = null
) {
    val isForBonus = purchaseItem?.bottomDrawerObjectType == VOUCHER_BONUS
    val finalStatus =
        if (isForBonus) getGoldRedemptionStatusFromString(purchaseItem?.goldBonusTxnStatus) else curateLoadingStatus(
            purchaseItem?.getManualPaymentStatus() ?: GoldRedemptionManualPaymentStatus.FAILED,
            purchaseItem?.getManualOrderStatus() ?: GoldRedemptionManualPaymentStatus.FAILED
        )
    Card(
        Modifier
            .fillMaxWidth()
            .then(if (navigate != null) Modifier.clickable {
                purchaseItem?.let {
                    navigate(it)
                }
            } else Modifier),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = colorResource(
            id = com.jar.app.core_ui.R.color.color_2e2942
        ),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
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
                        text = purchaseItem?.amount.orEmpty(),
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
                        color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
                        modifier = Modifier
                            .padding(top = 4.dp, end = 6.dp)
                            .weight(1f),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                    Text(
                        text = purchaseItem?.amountQuantityString.orEmpty(),
                        style = JarTypography.body1,
                        fontSize = 12.sp,
                        color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
                        modifier = Modifier.padding(top = 4.dp),
                        maxLines = 1
                    )
                }
                Row (modifier = Modifier.padding(top = 4.dp)) {
                    Text(
                        text = textForVoucherPurchaseStatusText(finalStatus, purchaseItem?.getRefundStatus()).orEmpty(),
                        style = JarTypography.body1,
                        color = colorForVoucherPurchaseStatusText(finalStatus, purchaseItem?.getRefundStatus()),
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .weight(1f),
                        maxLines = 1,
                    )
                    Text(
                        text = purchaseItem?.dateString ?: purchaseItem?.date.orEmpty(),
                        style = JarTypography.body1,
                        color = colorResource(id = com.jar.app.core_ui.R.color.color_776E94),
                        modifier = Modifier.padding(top = 2.dp),
                        fontSize = 12.sp,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

fun textForVoucherPurchaseStatusText(
    status: GoldRedemptionManualPaymentStatus?,
    refundStatus: RefundStatus? = null
): String? {
    if (refundStatus != null) {
        when (refundStatus) {
            RefundStatus.REFUNDED -> return "Successful"
            RefundStatus.REFUND_INITIATED,
            RefundStatus.REFUND_PENDING,
            RefundStatus.REFUND_PROCESSING, RefundStatus.REFUND_FAILED -> return "Initiated"
        }
    }
    status ?: return null
    return when (status) {
        GoldRedemptionManualPaymentStatus.SUCCESS , GoldRedemptionManualPaymentStatus.COMPLETED-> "Successful"
        GoldRedemptionManualPaymentStatus.PENDING, GoldRedemptionManualPaymentStatus.INITIALIZE, GoldRedemptionManualPaymentStatus.PROCESSING -> "Processing"
        GoldRedemptionManualPaymentStatus.FAILURE, GoldRedemptionManualPaymentStatus.FAILED -> "Failed"
        GoldRedemptionManualPaymentStatus.REFUNDED, GoldRedemptionManualPaymentStatus.REFUND_INITIATED, GoldRedemptionManualPaymentStatus.REFUND_PENDING, GoldRedemptionManualPaymentStatus.REFUND_PROCESSING, GoldRedemptionManualPaymentStatus.REFUND_FAILED -> "Failed"
    }
}
@Composable
fun colorForVoucherPurchaseStatusText(
    status: GoldRedemptionManualPaymentStatus?,
    refundStatus: RefundStatus?
): Color {
    if (refundStatus != null) {
        return colorResource(id = R.color.color_58DDC8)
    }
    return when (status) {
        GoldRedemptionManualPaymentStatus.SUCCESS, GoldRedemptionManualPaymentStatus.COMPLETED -> colorResource(id = R.color.color_58DDC8)
        GoldRedemptionManualPaymentStatus.PROCESSING, GoldRedemptionManualPaymentStatus.INITIALIZE, GoldRedemptionManualPaymentStatus.PENDING, null -> colorResource(id = R.color.color_EBB46A)
        GoldRedemptionManualPaymentStatus.FAILURE, GoldRedemptionManualPaymentStatus.FAILED -> colorResource(id = R.color.color_EB6A6E)
        GoldRedemptionManualPaymentStatus.REFUNDED, GoldRedemptionManualPaymentStatus.REFUND_INITIATED, GoldRedemptionManualPaymentStatus.REFUND_PENDING, GoldRedemptionManualPaymentStatus.REFUND_PROCESSING, GoldRedemptionManualPaymentStatus.REFUND_FAILED -> colorResource(id = R.color.color_EB6A6E)
    }
}
package com.jar.gold_redemption.impl.ui.cart_complete_payment

import androidx.annotation.DrawableRes
import com.jar.app.feature_gold_redemption.R
import com.jar.app.feature_gold_redemption.shared.domain.model.GoldRedemptionManualPaymentStatus


@DrawableRes
fun getGoldRedemptionHeaderIconForStatus(status: GoldRedemptionManualPaymentStatus): Int {
    return when (status) {
        GoldRedemptionManualPaymentStatus.COMPLETED, GoldRedemptionManualPaymentStatus.SUCCESS -> com.jar.app.core_ui.R.drawable.core_ui_icon_check_filled
        GoldRedemptionManualPaymentStatus.INITIALIZE, GoldRedemptionManualPaymentStatus.PROCESSING, GoldRedemptionManualPaymentStatus.PENDING -> R.drawable.feature_gold_redemption_hourglass
        GoldRedemptionManualPaymentStatus.FAILURE, GoldRedemptionManualPaymentStatus.FAILED -> R.drawable.feature_gold_redemption_failed_big
        GoldRedemptionManualPaymentStatus.REFUNDED, GoldRedemptionManualPaymentStatus.REFUND_INITIATED, GoldRedemptionManualPaymentStatus.REFUND_PENDING, GoldRedemptionManualPaymentStatus.REFUND_PROCESSING, GoldRedemptionManualPaymentStatus.REFUND_FAILED -> R.drawable.feature_gold_redemption_purple_circle
    }
}
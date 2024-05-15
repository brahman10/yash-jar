package com.jar.gold_redemption.impl.ui.cart_complete_payment

import com.jar.app.feature_gold_redemption.shared.domain.model.GoldRedemptionManualPaymentStatus

fun shouldShowMyOrdersButton(
    finalStatus: GoldRedemptionManualPaymentStatus,
    paymentStatus: GoldRedemptionManualPaymentStatus?,
    orderStatus: GoldRedemptionManualPaymentStatus?
): Boolean {
    if (paymentStatus in setOf(GoldRedemptionManualPaymentStatus.FAILED, GoldRedemptionManualPaymentStatus.FAILURE)) {
        return false
    } else {
        return true
    }
}
fun shouldContinueShoppingButton(
    finalStatus: GoldRedemptionManualPaymentStatus,
    paymentStatus: GoldRedemptionManualPaymentStatus?,
    orderStatus: GoldRedemptionManualPaymentStatus?
): ContinueShoppingButtonPlacement {
    if (paymentStatus in setOf(GoldRedemptionManualPaymentStatus.SUCCESS, GoldRedemptionManualPaymentStatus.COMPLETED)
        && orderStatus in setOf(GoldRedemptionManualPaymentStatus.FAILED, GoldRedemptionManualPaymentStatus.FAILURE)
    ) {
        return ContinueShoppingButtonPlacement.UP
    } else if (paymentStatus in setOf(GoldRedemptionManualPaymentStatus.FAILED, GoldRedemptionManualPaymentStatus.FAILURE)) {
        return ContinueShoppingButtonPlacement.DOWN
    }        else {
        return ContinueShoppingButtonPlacement.NONE
    }
}

enum class ContinueShoppingButtonPlacement {
    NONE, UP, DOWN
}
package com.jar.app.feature_gold_redemption.shared.domain.model

enum class GoldRedemptionManualPaymentStatus {
    SUCCESS,
    PENDING,
    PROCESSING,
    FAILURE,
    FAILED,
    COMPLETED,
    INITIALIZE,
    REFUNDED, REFUND_INITIATED, REFUND_PENDING, REFUND_PROCESSING, REFUND_FAILED,
}

fun getGoldRedemptionStatusForAnalytics(status: GoldRedemptionManualPaymentStatus): GoldRedemptionManualPaymentStatus {
    return when (status) {
        GoldRedemptionManualPaymentStatus.SUCCESS, GoldRedemptionManualPaymentStatus.COMPLETED -> GoldRedemptionManualPaymentStatus.SUCCESS
        GoldRedemptionManualPaymentStatus.INITIALIZE, GoldRedemptionManualPaymentStatus.PROCESSING, GoldRedemptionManualPaymentStatus.PENDING -> GoldRedemptionManualPaymentStatus.PENDING
        GoldRedemptionManualPaymentStatus.FAILED, GoldRedemptionManualPaymentStatus.FAILURE -> GoldRedemptionManualPaymentStatus.FAILURE
        GoldRedemptionManualPaymentStatus.REFUNDED -> GoldRedemptionManualPaymentStatus.REFUNDED
        GoldRedemptionManualPaymentStatus.REFUND_PENDING, GoldRedemptionManualPaymentStatus.REFUND_PROCESSING, GoldRedemptionManualPaymentStatus.REFUND_INITIATED -> GoldRedemptionManualPaymentStatus.REFUND_PENDING
        GoldRedemptionManualPaymentStatus.REFUND_FAILED -> GoldRedemptionManualPaymentStatus.REFUND_FAILED
    }
}
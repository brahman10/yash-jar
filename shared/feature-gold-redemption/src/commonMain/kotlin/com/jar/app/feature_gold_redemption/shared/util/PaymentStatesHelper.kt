package com.jar.app.feature_gold_redemption.shared.util

import com.jar.app.feature_gold_redemption.shared.domain.model.GoldRedemptionManualPaymentStatus
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus


fun curateLoadingStatus(manualPaymentStatus: GoldRedemptionManualPaymentStatus,
                                 manualOrderStatus: GoldRedemptionManualPaymentStatus
): GoldRedemptionManualPaymentStatus {
    return when (manualPaymentStatus) {
        GoldRedemptionManualPaymentStatus.SUCCESS, GoldRedemptionManualPaymentStatus.COMPLETED -> {
            return manualOrderStatus ?: GoldRedemptionManualPaymentStatus.PENDING
        }

        GoldRedemptionManualPaymentStatus.FAILURE, GoldRedemptionManualPaymentStatus.FAILED -> manualPaymentStatus
        GoldRedemptionManualPaymentStatus.PENDING, GoldRedemptionManualPaymentStatus.INITIALIZE -> manualPaymentStatus
        null -> GoldRedemptionManualPaymentStatus.PENDING
        GoldRedemptionManualPaymentStatus.PROCESSING -> manualPaymentStatus
        GoldRedemptionManualPaymentStatus.REFUNDED, GoldRedemptionManualPaymentStatus.REFUND_INITIATED, GoldRedemptionManualPaymentStatus.REFUND_PENDING, GoldRedemptionManualPaymentStatus.REFUND_PROCESSING, GoldRedemptionManualPaymentStatus.REFUND_FAILED, -> GoldRedemptionManualPaymentStatus.FAILED
    }
}


fun curateLoadingStatus(data: FetchManualPaymentStatusResponse?): GoldRedemptionManualPaymentStatus {
    val status = data?.getManualPaymentStatus()
    return when (status) {
        ManualPaymentStatus.SUCCESS -> {
            return getGoldRedemptionStatusFromString(data.createVoucherOrderResponse?.voucherOrderStatus) ?: GoldRedemptionManualPaymentStatus.PENDING
        }
        ManualPaymentStatus.FAILURE ->  getGoldRedemptionStatusFromPaymentStatus(status)
        ManualPaymentStatus.PENDING ->  getGoldRedemptionStatusFromPaymentStatus(status)
        null -> GoldRedemptionManualPaymentStatus.PENDING
    }
}

fun getGoldRedemptionStatusFromPaymentStatus(status: ManualPaymentStatus): GoldRedemptionManualPaymentStatus {
    return when (status) {
        ManualPaymentStatus.SUCCESS -> GoldRedemptionManualPaymentStatus.SUCCESS
        ManualPaymentStatus.PENDING -> GoldRedemptionManualPaymentStatus.PENDING
        ManualPaymentStatus.FAILURE -> GoldRedemptionManualPaymentStatus.FAILURE
    }
}

fun getGoldRedemptionStatusFromString(status: String?): GoldRedemptionManualPaymentStatus? {
    return status?.let {
        if (it == "COMPLETED") {
            GoldRedemptionManualPaymentStatus.SUCCESS
        }
        GoldRedemptionManualPaymentStatus.valueOf(it)
    } ?: run {
        null
    }
}

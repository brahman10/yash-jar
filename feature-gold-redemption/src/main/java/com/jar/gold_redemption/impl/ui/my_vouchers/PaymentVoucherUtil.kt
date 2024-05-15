package com.jar.gold_redemption.impl.ui.my_vouchers

import com.jar.app.feature_one_time_payments_common.shared.Voucher
import com.jar.app.feature_gold_redemption.shared.domain.model.GoldRedemptionManualPaymentStatus
import com.jar.app.feature_gold_redemption.shared.data.network.model.UserVoucher


internal fun curateFromPaymentsVouchers(
    voucher: Voucher,
    productType: String?,
    finalStatus: GoldRedemptionManualPaymentStatus
): UserVoucher {
    return UserVoucher(
        amount = voucher.amount,
        code = voucher.code,
        imageUrl = voucher.imageUrl,
        voucherName = voucher.voucherName,
        calendarUrl = null,
        noOfDaysToRedeem = null,
        validTillText = null,
        creationDate = null,
        viewDetails = null,
        myVouchersType = "ACTIVE",
        voucherExpiredText = null,
        voucherId = null,
        voucherProcessingText = null,
        orderId = "",
        currentState = productType,
        quantity = null,
    )
}
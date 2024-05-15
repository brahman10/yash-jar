package com.jar.gold_redemption.impl.ui.cart_complete_payment

import android.content.Context
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.base.util.orFalse
import com.jar.app.core_compose_ui.views.LabelAndValueCompose
import com.jar.app.feature_one_time_payments_common.shared.CreateVoucherOrderResponse
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_one_time_payments_common.shared.PaymentOrderDetails
import com.jar.app.feature_gold_redemption.shared.domain.model.GoldRedemptionManualPaymentStatus
import com.jar.app.feature_one_time_payments_common.shared.Voucher
import com.jar.app.core_compose_ui.views.payments.TimelineViewData
import com.jar.app.core_compose_ui.views.payments.TransactionStatus
import com.jar.app.feature_gold_redemption.shared.data.network.model.UserVoucher
import com.jar.gold_redemption.impl.ui.my_vouchers.curateFromPaymentsVouchers
import com.jar.app.feature_gold_redemption.shared.data.network.model.GoldRedemptionTransactionData
import com.jar.app.feature_gold_redemption.shared.util.getGoldRedemptionStatusFromPaymentStatus
import com.jar.app.feature_gold_redemption.shared.util.getGoldRedemptionStatusFromString
import java.lang.ref.WeakReference

fun getCoreComposeTransactionStatus(status: GoldRedemptionManualPaymentStatus): TransactionStatus {
    return when (status) {
        GoldRedemptionManualPaymentStatus.COMPLETED, GoldRedemptionManualPaymentStatus.SUCCESS -> TransactionStatus.SUCCESS
        GoldRedemptionManualPaymentStatus.INITIALIZE, GoldRedemptionManualPaymentStatus.PROCESSING, GoldRedemptionManualPaymentStatus.PENDING -> TransactionStatus.PENDING
        GoldRedemptionManualPaymentStatus.FAILED, GoldRedemptionManualPaymentStatus.FAILURE,
        GoldRedemptionManualPaymentStatus.REFUNDED, GoldRedemptionManualPaymentStatus.REFUND_INITIATED, GoldRedemptionManualPaymentStatus.REFUND_PENDING, GoldRedemptionManualPaymentStatus.REFUND_PROCESSING,
        GoldRedemptionManualPaymentStatus.REFUND_FAILED -> TransactionStatus.FAILED
    }
}

internal fun curateForPaymentStatus(
    title: String?,
    retryText: String?,
    dateString: String?,
    status: GoldRedemptionManualPaymentStatus,
    isRetryButtonAllowed: Boolean,
    isRefreshButtonAllowed: Boolean,
    weakReference: WeakReference<Context?>,
): TimelineViewData {
    return TimelineViewData(
        getCoreComposeTransactionStatus(status),
        if (isRetryButtonAllowed && status == GoldRedemptionManualPaymentStatus.PENDING) {
            weakReference.get()
                ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_payment)
                ?: "Payment"
        } else {
            title ?: weakReference.get()
                ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_payment)
            ?: "Payment"
        },
        if (status in setOf(
                GoldRedemptionManualPaymentStatus.SUCCESS,
                GoldRedemptionManualPaymentStatus.FAILURE,
                GoldRedemptionManualPaymentStatus.FAILED,
                GoldRedemptionManualPaymentStatus.REFUNDED,
                GoldRedemptionManualPaymentStatus.REFUND_INITIATED,
                GoldRedemptionManualPaymentStatus.REFUND_PENDING,
                GoldRedemptionManualPaymentStatus.REFUND_PROCESSING,
                GoldRedemptionManualPaymentStatus.REFUND_FAILED,
            )
        ) dateString else null,
        refreshText = if (status == GoldRedemptionManualPaymentStatus.FAILURE) weakReference.get()
            ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_your_payment_was_unsuccessful)
            .orEmpty() else retryText,
        isRetryButtonEnabled = isRetryButtonAllowed,
        isRefreshButtonShown = isRefreshButtonAllowed
    )
}

internal fun curateForOrderStatus(
    title: String?,
    retryText: String?,
    dateString: String?,
    status: GoldRedemptionManualPaymentStatus? = null,
    weakReference: WeakReference<Context?>,
    paymentStatus: GoldRedemptionManualPaymentStatus
): TimelineViewData {
    return if (status == null || paymentStatus in setOf(
            GoldRedemptionManualPaymentStatus.FAILED,
            GoldRedemptionManualPaymentStatus.FAILURE,
            GoldRedemptionManualPaymentStatus.PENDING,
            GoldRedemptionManualPaymentStatus.PROCESSING
        )
    ) {
        TimelineViewData(
            null,
            weakReference.get()
                ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_voucher_purchase)
                .orEmpty(),
            "",
            isRetryButtonEnabled = false
        )
    } else {
        TimelineViewData(
            getCoreComposeTransactionStatus(status),
            title ?: weakReference.get()
                ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_voucher_purchase)
                .orEmpty(),
            dateString,
            isRetryButtonEnabled = false
        )
    }
}

internal fun curateStatus(
    data: GoldRedemptionTransactionData,
    weakReference: WeakReference<Context?>,
    paymentTitle: String? = null,
    orderTitle: String? = null,
    isForBonus: Boolean,
): List<TimelineViewData> {
    val list = mutableListOf<TimelineViewData>()

    val manualPaymentStatus = data.getManualPaymentStatus()
    val curateForPaymentStatus = curateForPaymentStatus(
        paymentTitle,
        data?.paymentProcessingText,
        data.dateString ?: data.paymentOrderDetails?.placedOn,
        manualPaymentStatus,
        false,
        data.refreshAllowed.orFalse(),
        weakReference = weakReference
    )

    list.add(
        curateForPaymentStatus
    )

    list.add(
        curateForOrderStatus(
            orderTitle,
            null,
            curateDateString(data.dateString, data.getManualOrderStatus(isForBonus)),
            data.getManualOrderStatus(isForBonus),
            weakReference,
            paymentStatus = manualPaymentStatus
        )
    )
    return list
}

fun shouldShowPaymentRetryButton(paymentStatus: GoldRedemptionManualPaymentStatus): Boolean {
    return (paymentStatus in setOf(
        GoldRedemptionManualPaymentStatus.FAILURE,
        GoldRedemptionManualPaymentStatus.FAILED
    ))
}

internal fun curateStatus(
    data: FetchManualPaymentStatusResponse?,
    weakReference: WeakReference<Context?>,
    retryText: String? = null
): List<TimelineViewData> {
    val list = mutableListOf<TimelineViewData>()
    if (data == null) return list

    val paymentStatus = getGoldRedemptionStatusFromPaymentStatus(data.getManualPaymentStatus())
    list.add(
        curateForPaymentStatus(
            weakReference.get()?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_payment) ?: "Payment",
            data?.createVoucherOrderResponse?.congratulationsText ?: retryText.orEmpty(),
            data?.createVoucherOrderResponse?.paymentOrderDetails?.placedOn,
            paymentStatus,
            shouldShowPaymentRetryButton(paymentStatus),
            showShowPaymentRefreshButton(paymentStatus),
            weakReference
        )
    )
    list.add(
        curateForOrderStatus(
            null,
            null,
            curateDateString(
                data.createVoucherOrderResponse?.paymentOrderDetails?.placedOn,
                getGoldRedemptionStatusFromString(data?.createVoucherOrderResponse?.voucherOrderStatus)
            ),
            getGoldRedemptionStatusFromString(data?.createVoucherOrderResponse?.voucherOrderStatus),
            weakReference,
            paymentStatus = paymentStatus
        )
    )
    return list
}

fun showShowPaymentRefreshButton(paymentStatus: GoldRedemptionManualPaymentStatus): Boolean {
    return (paymentStatus in setOf(
        GoldRedemptionManualPaymentStatus.PROCESSING,
        GoldRedemptionManualPaymentStatus.PENDING
    ))
}

fun curateDateString(
    placedOn: String?,
    toGoldRedemptionStatus: GoldRedemptionManualPaymentStatus?
): String? {
    if (toGoldRedemptionStatus in setOf(
            GoldRedemptionManualPaymentStatus.PROCESSING,
            GoldRedemptionManualPaymentStatus.FAILED,
            GoldRedemptionManualPaymentStatus.FAILURE,
            GoldRedemptionManualPaymentStatus.PENDING
        )
    ) {
        return ""
    }
    return placedOn
}

internal fun curateVoucherCardList(
    voucherList: List<Voucher?>?,
    finalStatus: GoldRedemptionManualPaymentStatus,
    productType: String?
): List<UserVoucher> {
    val list = mutableListOf<UserVoucher>()
    voucherList?.let {
        it.forEach { voucher ->
            voucher?.let {
                curateFromPaymentsVouchers(it, productType, finalStatus)?.let {
                    list.add(it)
                }
            }
        }
    }
    return list
}

internal fun curateVoucherDetailsList(
    data: CreateVoucherOrderResponse?,
    weakReference: WeakReference<Context?>,
): List<LabelAndValueCompose> {
    val list = mutableListOf<LabelAndValueCompose>()
    data?.voucherOrderDetails?.brandName?.let {
        list.add(
            LabelAndValueCompose(
                weakReference.get()
                    ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_brand)
                    .orEmpty(), it
            )
        )
    }
    data?.voucherOrderDetails?.productType?.let {
        list.add(
            LabelAndValueCompose(
                weakReference.get()
                    ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_voucher_type)
                    .orEmpty(), mapToVoucherTypeString(it, weakReference)
            )
        )
    }
    data?.voucherOrderDetails?.validity?.let {
        list.add(
            LabelAndValueCompose(
                weakReference.get()
                    ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_validity)
                    .orEmpty(), it
            )
        )
    }
    data?.voucherOrderDetails?.amount?.let {
        list.add(
            LabelAndValueCompose(
                weakReference.get()
                    ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_amount)
                    .orEmpty(), "₹" + it.getFormattedAmount()
            )
        )
    }
    data?.voucherOrderDetails?.quantity?.let {
        list.add(
            LabelAndValueCompose(
                weakReference.get()
                    ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_quantity)
                    .orEmpty(), "${it}"
            )
        )
    }

    return list.toList()
}

fun mapToVoucherTypeString(it: String, weakReference: WeakReference<Context?>): String {
    return when (it.uppercase()) {
        "DIAMOND" -> weakReference.get()
            ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_diamond_voucher)
            .orEmpty()

        "GOLD" -> weakReference.get()
            ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_gold_voucher)
            .orEmpty()

        else -> it
    }
}

internal fun curatePaymentDetailsList(
    paymentOrderDetails: PaymentOrderDetails?,
    weakReference: WeakReference<Context?>
): List<LabelAndValueCompose> {
    val list = mutableListOf<LabelAndValueCompose>()
    paymentOrderDetails?.apply {

        this?.paymentOrderId?.let {
            list.add(
                LabelAndValueCompose(
                    weakReference.get()
                        ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_order_id)
                        ?: "Order ID", it, showCopyToClipBoardIconAndTruncate = true
                )
            )
        } ?: run {
            this?.transactionId?.let {
                list.add(
                    LabelAndValueCompose(
                        weakReference.get()
                            ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_transaction_id)
                            ?: "Transaction Id", it, showCopyToClipBoardIconAndTruncate = true
                    )
                )
            }
        }
        this?.placedOn?.let {
            list.add(
                LabelAndValueCompose(
                    weakReference.get()
                        ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_placed_on)
                        ?: "Placed on", it
                )
            )
        }
        this?.paidVia?.let {
            list.add(
                LabelAndValueCompose(
                    weakReference.get()
                        ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_paid_via)
                        ?: "Paid via", it
                )
            )
        }
        this?.upiId?.trim()?.takeIf { !it.isNullOrBlank() }?.let {
            list.add(
                LabelAndValueCompose(
                    weakReference.get()
                        ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_upi_id)
                        ?: "UPI ID", it
                )
            )
        }
    }
    return list
}

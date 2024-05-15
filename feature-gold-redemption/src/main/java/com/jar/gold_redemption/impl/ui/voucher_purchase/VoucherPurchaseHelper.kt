package com.jar.gold_redemption.impl.ui.voucher_purchase

import android.content.Context
import com.jar.app.core_compose_ui.views.ExpandableCardModel
import com.jar.app.core_compose_ui.views.LabelAndValueCompose
import com.jar.app.feature_gold_redemption.R
import com.jar.app.feature_one_time_payments_common.shared.PaymentOrderDetails
import com.jar.app.feature_gold_redemption.shared.data.network.model.RefundDetails
import com.jar.app.feature_gold_redemption.shared.data.network.model.VoucherPurchaseAPIData
import java.lang.ref.WeakReference


internal fun computeAvailability(it: VoucherPurchaseAPIData?): String {
    return when (it?.onlineRedemptionText.isNullOrBlank().compareTo(false) + it?.inStoreRedemptionText.isNullOrBlank().compareTo(false)) {
        0 -> "both_online_offline"
        1 -> if (it?.onlineRedemptionText.isNullOrBlank()) "only_offline" else "only_online"
        else -> "None"
    }
}

fun curateRefundDetails(
    refundDetails: RefundDetails,
    weakReference: WeakReference<Context?>,
    showDrawable: Boolean = true,
    ): List<ExpandableCardModel>  {
    val context = weakReference.get()
    return listOf(ExpandableCardModel(
        0,
        context?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_refund_details).orEmpty(),
        data = buildLabelComposeForRefundDetails(refundDetails, context),
        drawableRes = if (showDrawable) R.drawable.feature_gold_redemption_invoice else null
    ))
}

fun buildLabelComposeForRefundDetails(refundDetails: RefundDetails, context: Context?): List<LabelAndValueCompose> {
    val list = mutableListOf<LabelAndValueCompose>()
    refundDetails.transactionId?.let {
        list.add(LabelAndValueCompose(context?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_transaction_id)?: "", it, showCopyToClipBoardIconAndTruncate = true))
    }
    refundDetails.refundedOn?.let {
        list.add(LabelAndValueCompose(context?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_refunded_on)?: "", it))
    }
    refundDetails.refundedTo?.let {
        list.add(LabelAndValueCompose(context?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_refunded_to)?: "", it))
    }
    return list
}

fun curateVoucherPurchaseFaqs(
    howToRedeem: List<String?>?,
    tnc: List<String?>?,
    paymentOrderDetails: PaymentOrderDetails?,
    showDrawable: Boolean = true,
    amount: Int? = null,
    weakReference: WeakReference<Context?>
): List<ExpandableCardModel> {
    val list = mutableListOf<ExpandableCardModel>()
    val context = weakReference.get()
    howToRedeem?.takeIf { !it.isNullOrEmpty() }?.let {
        list.add(
            ExpandableCardModel(
                0,
                context?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_how_to_redeem) ?: "How to redeem",
                listToString(howToRedeem),
                drawableRes = if (showDrawable) R.drawable.feature_gold_redemption_paper else null
            )
        )
    }
    tnc?.takeIf { !it.isNullOrEmpty() }?.let {
        val listToString = listToString(tnc)
        if (listToString.isNotEmpty())
            list.add(
                ExpandableCardModel(
                    1,
                    context?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_terms_conditions) ?: "Terms & conditions",
                    listToString,
                    drawableRes = if (showDrawable) R.drawable.feature_gold_redemption_info_square else null
                )
            )
    }
    paymentOrderDetails?.let {
        val generateList = generateList(it, amount, weakReference)
        if (generateList.isNotEmpty())
            list.add(
                ExpandableCardModel(
                    2,
                    context?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_order_details).orEmpty(),
                    null,
                    data = generateList,
                    drawableRes = if (showDrawable) R.drawable.feature_gold_redemption_invoice else null
                )
            )
    }
    return list
}

fun generateList(
    paymentOrderDetails: PaymentOrderDetails?, amount: Int?,
    weakReference: WeakReference<Context?>
): List<LabelAndValueCompose> {
    val list = mutableListOf<LabelAndValueCompose>()
    val context = weakReference.get()
    paymentOrderDetails?.apply {
//        amount?.let {
//            list.add(LabelAndValueCompose(context?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_amount)?: "Amount", "₹" + it.getFormattedAmount()))
//        }

        this?.transactionId?.let {
            list.add(LabelAndValueCompose(context?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_transaction_id)?: "Transaction Id", it, showCopyToClipBoardIconAndTruncate = true))
        } ?: run {
            this?.paymentOrderId?.let {
                list.add(LabelAndValueCompose(context?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_order_id)?: "Order ID", it, showCopyToClipBoardIconAndTruncate = true))
            }
        }
        this?.placedOn?.let {
            list.add(LabelAndValueCompose(context?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_placed_on)?: "Placed on", it))
        }
        this?.paidVia?.let {
            list.add(LabelAndValueCompose(context?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_paid_via)?: "Paid via", it))
        }
        this?.upiId?.let {
            list.add(LabelAndValueCompose(context?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_upi_id)?: "UPI ID", it))
        }
    }
    return list
}

fun listToString(list: List<String?>?): String {
    return list?.joinToString(separator = "\n") { "${it}\n".orEmpty() }.orEmpty()
}
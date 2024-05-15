package com.jar.app.feature_gold_redemption.shared.data.network.model

import com.jar.app.feature_gold_redemption.shared.domain.model.GoldRedemptionManualPaymentStatus
import kotlinx.serialization.SerialName
import com.jar.app.feature_one_time_payments_common.shared.PaymentOrderDetails
import kotlinx.serialization.Serializable

@Serializable
data class GoldRedemptionTransactionData(
    @SerialName("amount")
    val amount: String? = null,
    @SerialName("brandName")
    val brandName: String? = null,
    @SerialName("dateString")
    val dateString: String? = null,
    @SerialName("voucherBonusAmountString")
    val voucherBonusAmountString: String? = null,
    @SerialName("headerDateString")
    val headerDateString: String? = null,
    @SerialName("voucherImageUrl")
    val imageUrl: String? = null,
    @SerialName("bonusQuantityMultipliedAmountString")
     val bonusQuantityMultipliedAmountString: String? = null,
    @SerialName("voucherBonusImageUrl")
    val voucherBonusImageUrl: String? = null,
    @SerialName("paidForVouchersString")
    val paidForVouchersString: String? = null,
    @SerialName("paymentOrderId")
    val paymentOrderId: String? = null,
    @SerialName("paymentOrderDetails")
    val paymentOrderDetails: PaymentOrderDetails? = null,
    @SerialName("paymentProcessingText")
    val paymentProcessingText: String? = null,
    @SerialName("quantityMultipliedAmountString")
    val quantityMultipliedAmountString: String? = null,
    @SerialName("refreshAllowed")
    val refreshAllowed: Boolean? = null,
    @SerialName("txnStatus")
    val txnStatus: String? = null,
    @SerialName("voucherOrderStatus")
    val voucherOrderStatus: String? = null,
    @SerialName("refundDetails")
    val refundDetails: RefundDetails? = null,
    @SerialName("goldBonusTransactionStatus")
    val goldBonusTransactionStatus: String? = null,
    @SerialName("voucherTypeString")
    val voucherTypeString: String? = null,
    @SerialName("voucherPurchaseSummaryList")
    val voucherList: List<PurchaseItemData>? = null,
) {
    fun getManualPaymentStatus(): GoldRedemptionManualPaymentStatus {
        return txnStatus?.let {
            GoldRedemptionManualPaymentStatus.valueOf(it)
        } ?: run {
            GoldRedemptionManualPaymentStatus.FAILURE
        }
    }
    fun getManualOrderStatus(isForBonus: Boolean? = null): GoldRedemptionManualPaymentStatus {
        var status = voucherOrderStatus
        if (isForBonus == true) {
            status = goldBonusTransactionStatus
        }
        val s = (status)?.let {
            GoldRedemptionManualPaymentStatus.valueOf(it)
        } ?: run {
            GoldRedemptionManualPaymentStatus.PENDING
        }

        return when (s) {
            GoldRedemptionManualPaymentStatus.COMPLETED, GoldRedemptionManualPaymentStatus.SUCCESS -> GoldRedemptionManualPaymentStatus.SUCCESS
            GoldRedemptionManualPaymentStatus.PENDING, GoldRedemptionManualPaymentStatus.INITIALIZE, GoldRedemptionManualPaymentStatus.PROCESSING -> GoldRedemptionManualPaymentStatus.PENDING
            GoldRedemptionManualPaymentStatus.FAILURE, GoldRedemptionManualPaymentStatus.FAILED -> GoldRedemptionManualPaymentStatus.FAILURE
            GoldRedemptionManualPaymentStatus.REFUNDED, GoldRedemptionManualPaymentStatus.REFUND_INITIATED, GoldRedemptionManualPaymentStatus.REFUND_PENDING, GoldRedemptionManualPaymentStatus.REFUND_PROCESSING, GoldRedemptionManualPaymentStatus.REFUND_FAILED -> GoldRedemptionManualPaymentStatus.SUCCESS
        }
    }
}
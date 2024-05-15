package com.jar.app.feature_gold_redemption.shared.data.network.model


import kotlinx.serialization.SerialName
import com.jar.app.feature_gold_redemption.shared.domain.model.GoldRedemptionManualPaymentStatus
import com.jar.app.feature_gold_redemption.shared.domain.model.RefundStatus
import kotlinx.serialization.Serializable

@Serializable
data class PurchaseItemData(
    @SerialName("amount")
    val amount: String? = null,
    @SerialName("orderId")
    val voucherId: String? = null,
    @SerialName("amountQuantityString")
    val amountQuantityString: String? = null,
    @SerialName("date")
    val date: String? = null,
    @SerialName("dateString")
    val dateString: String? = null,
    @SerialName("desc")
    val desc: String? = null,
    @SerialName("refundStatus")
    val refundStatus: String? = null,
    @SerialName("bottomDrawerObjectType")
    val bottomDrawerObjectType: String? = null,
    @SerialName("imageUrl")
    val imageUrl: String? = null,
    @SerialName("quantity")
    val quantity: Int? = null,
    @SerialName("txnStatus")
    val txnStatus: String? = null,
    @SerialName("voucherOrderStatus")
    val voucherOrderStatus: String? = null,
    @SerialName("goldBonusTxnStatus")
    val goldBonusTxnStatus: String? = null,
    @SerialName("title")
    val title: String? = null
) {
    fun getRefundStatus(): RefundStatus? {
        if (refundStatus.isNullOrEmpty()) return null
        return RefundStatus.valueOf(refundStatus)
    }
    fun getManualPaymentStatus(): GoldRedemptionManualPaymentStatus {
        val status = txnStatus ?: goldBonusTxnStatus
        if (status.equals("PROCESSING", true)) GoldRedemptionManualPaymentStatus.PENDING
        return GoldRedemptionManualPaymentStatus.valueOf(status ?: "FAILURE")
    }
    fun getManualOrderStatus(): GoldRedemptionManualPaymentStatus {
        val status = voucherOrderStatus
        if (status.equals("PROCESSING", true)) GoldRedemptionManualPaymentStatus.PENDING
        return GoldRedemptionManualPaymentStatus.valueOf(status ?: "FAILURE")
    }
}
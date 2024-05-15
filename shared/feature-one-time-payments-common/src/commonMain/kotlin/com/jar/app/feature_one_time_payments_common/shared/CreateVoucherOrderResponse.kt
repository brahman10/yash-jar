package com.jar.app.feature_one_time_payments_common.shared

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class CreateVoucherOrderResponse(
    @SerialName("bonusGoldText")
    val bonusGoldText: String? = null,
    @SerialName("paymentOrderDetails")
    val paymentOrderDetails: PaymentOrderDetails? = null,
    @SerialName("voucherList")
    val voucherList: List<Voucher?>? = null,
    @SerialName("voucherOrderDetails")
    val voucherOrderDetails: VoucherOrderDetails? = null,
    @SerialName("voucherOrderStatus")
    val voucherOrderStatus: String? = null,
    @SerialName("congratulationsText")
    val congratulationsText: String? = null,
    @SerialName("dateString")
    val dateString: String? = null,
    @SerialName("processingText")
    val processingText: String? = null,
    @SerialName("refreshAllowed")
    val refreshAllowed: Boolean? = null,
    @SerialName("refundDetails")
    val refundDetails: String? = null,
    @SerialName("retryAllowed")
    val retryAllowed: Boolean? = null,
    @SerialName("voucherPurchaseFailedString")
    val voucherPurchaseFailedString: String? = null,
    @SerialName("voucherPurchaseProcessingString")
    val voucherPurchaseProcessingString: String? = null,
) : Parcelable
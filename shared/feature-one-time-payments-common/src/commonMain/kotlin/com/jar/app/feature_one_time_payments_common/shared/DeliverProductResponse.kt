package com.jar.app.feature_one_time_payments_common.shared

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class DeliverProductResponse(

    @SerialName("amountToBePaid")
    val amountToBePaid: Double? = null,

    @SerialName("courierCompany")
    val courierCompany: String? = null,

    @SerialName("created_at")
    val created_at: String? = null,

    @SerialName("currentGoldBal")
    val currentGoldBal: Double? = null,

    @SerialName("estimated_dispatch")
    val estimated_dispatch: String? = null,

    @SerialName("giftingId")
    val giftingId: String? = null,

    @SerialName("goldVolume")
    val goldVolume: Float? = null,

    @SerialName("invoiceLink")
    val invoiceLink: String? = null,

    @SerialName("isBalanceAvailable")
    val isBalanceAvailable: Boolean? = null,

    @SerialName("isInvoiceSMSSent")
    val isInvoiceSMSSent: Boolean? = null,

    @SerialName("message")
    val message: String? = null,

    @SerialName("orderId")
    val orderId: String? = null,

    @SerialName("status")
    val status: Int? = null,

    @SerialName("statusErrorMessage")
    val statusErrorMessage: String? = null,

    @SerialName("statusType")
    val statusType: String? = null,

    @SerialName("trackingId")
    val trackingId: String? = null,

    @SerialName("trackingLink")
    val trackingLink: String? = null,

    @SerialName("deliveryMakingCharge")
    val deliveryMakingCharge: Double? = null,

    // Only for gold delivery v2 flow
    @SerialName("orders")
    val orderDetails: List<OrderDetails?>? = null,

    @SerialName("label")
    val label: String? = null,

    @SerialName("quantity")
    val quantity: String? = null,

    @SerialName("discountedPrice")
    val discountedPrice: Float? = null
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class OrderDetails(
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("amount")
    val amount: Double? = null,
    @SerialName("volume")
    val volume: Double? = null,
    @SerialName("discountOnTotal")
    val discountOnTotal: Double? = null,
    @SerialName("orderId")
    val orderId: String? = null,
    @SerialName("assetTransactionId")
    val assetTransactionId: String? = null,
    @SerialName("assetSourceType")
    val assetSourceType: String? = null,
    @SerialName("txnStatus")
    val txnStatus: String? = null,
    @SerialName("label")
    val label: String? = null,
    @SerialName("quantity")
    val quantity: String? = null
) : Parcelable
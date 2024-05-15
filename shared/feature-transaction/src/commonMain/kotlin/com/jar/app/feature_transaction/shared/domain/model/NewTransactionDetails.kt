package com.jar.app.feature_transaction.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
data class NewTransactionDetails(
    @SerialName("currentStatus")
    val currentStatus: String? = null,
    @SerialName("txnRoutine")
    val txnRoutine: List<TxnRoutine>? = null,
    @SerialName("txnDetailsBottomObjects")
    val txnDetailsBottomObjects: List<TxnDetails>? = null,
    @SerialName("headers")
    val headers: TransactionHeaders? = null,
    @SerialName("showGiftReminder")
    val showGiftingReminder: Boolean? = null,
    @SerialName("shouldShowContactUs")
    val shouldShowContactUs: Boolean? = null,
    @SerialName("refreshAllowed")
    val refreshAllowed: Boolean? = null,
    @SerialName("invoiceAvailable")
    val invoiceAvailable: Boolean? = null,
    @SerialName("gift")
    val gift: Boolean? = null,
    @SerialName("retryAllowed")
    val retryAllowed: Boolean? = null,

    @SerialName("couponCodeDetails")
    val couponCodeDetails: CouponCodeData? = null,
    @SerialName("weeklyChallengeId")
    val challengeId: String? = null,
    @SerialName("giftingDetails")
    val giftingDetails: GoldGiftingData? = null,
    @SerialName("invoiceLink")
    val invoiceLink: String? = null,
    @SerialName("retryButtonTxt")
    val retryButtonTxt: String? = null,
    @SerialName("txnType")
    val txnType: String? = null,
    @SerialName("roundoffCount")
    val roundoffCount: Int? = null,
    @SerialName("trackingInfo")
    val trackingInfo: TxnTrackingData? = null,
    @SerialName("failureReason")
    val failureReason: String? = null,
    @SerialName("deliveryProductObject")
    val productDetails: ProductData? = null,
    @SerialName("jarWinningsUsedText")
    val jarWinningsUsedText: String? = null,
    @SerialName("statusInfo")
    val statusInfo: StatusInfo? = null,
    @SerialName("leasingTxnDetails")
    val leasingTnxDetails: LeasingTnxDetails? = null,
    @SerialName("savingsBreakDownInfo")
    val savingTxnDetails: SavingTxnDetails? = null,
    @SerialName("pauseInfo")
    val pauseTxnDetails: PauseTxnDetails? = null,
    @SerialName("paymentInfo")
    val paymentDetails: PaymentDetails? = null,
    @SerialName("addressDetails")
    val addressDetails: AddressDetails? = null,
    @SerialName("paymentSource")
    val paymentSource: PaymentSource? = null,
) {
    fun getTxnRoutineDetails(): TxnRoutineDetails {
        return TxnRoutineDetails(
            invoiceAvailable = invoiceAvailable,
            invoiceLink = invoiceLink,
            refreshAllowed = refreshAllowed,
            retryAllowed = retryAllowed,
            showGiftingReminder = showGiftingReminder,
            retryButtonTxt = retryButtonTxt,
            status = currentStatus,
            txnRoutine = txnRoutine,
            txnType = txnType,
            failureReason = failureReason
        )
    }
}

@Serializable
data class AddressDetails(
    @SerialName("name")
    val name: String? = null,
    @SerialName("address")
    val address: String? = null
)

@Serializable
data class PaymentSource(
    @SerialName("paymentMethod")
    val paymentMethod: String? = null,
    @SerialName("payerVpa")
    val payerVpa: String? = null,
    @SerialName("lastFourDigits")
    val lastFourDigits: String? = null,
    @SerialName("imageUrl")
    val imageUrl: String? = null,
    @SerialName("userAmount")
    val userAmount: Double? = null,
    @SerialName("paymentAmount")
    val paymentAmount: Double? = null
)

@Serializable
data class PaymentDetails(
    @SerialName("amount")
    val amount: Double? = null,
    @SerialName("deliveryMakingCharge")
    val deliveryMakingCharge: Double? = null,
    @SerialName("gst")
    val gst: Float? = null,
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("inStock")
    val inStock: Boolean? = null,
    @SerialName("label")
    val label: String? = null,
    @SerialName("productId")
    val productId: Int? = null,
    @SerialName("quantity")
    val quantity: Int? = null,
    @SerialName("totalAmount")
    val totalAmount: Double? = null,
    @SerialName("volume")
    val volume: Double? = null
)
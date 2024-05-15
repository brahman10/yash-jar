package com.jar.app.feature_gold_delivery.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class GoldDeliveryTransactionState(
    @SerialName("amount")
    val amount: Double? = null,
    @SerialName("changedAmount")
    val changedAmount: Double? = null,
    @SerialName("differenceAmount")
    val differenceAmount: Double? = null,
    @SerialName("goldPriceChanged")
    val goldPriceChanged: Boolean? = null,
    @SerialName("orderId")
    val orderId: String? = null,
    @SerialName("refreshAllowed")
    val refreshAllowed: Boolean? = null,
    @SerialName("retryAllowed")
    val retryAllowed: Boolean? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("subTitle")
    val subTitle: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("txnType")
    val txnType: String? = null
) : Parcelable {
    fun getManualPaymentStatus(): com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus {
        return com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.valueOf(status ?: "FAILURE")
    }
}
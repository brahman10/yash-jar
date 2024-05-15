package com.jar.app.feature_transaction.shared.domain.model

import kotlinx.serialization.SerialName
import com.jar.app.core_base.util.BaseConstants

@kotlinx.serialization.Serializable
data class TxnTrackingData(
    @SerialName("trackingLink")
    val trackingLink: String? = null,

    @SerialName("trackingId")
    val trackingId: String? = null,

    @SerialName("courierCompany")
    val courierCompany: String? = null,

    @SerialName("status")
    val status: String? = null,

    @SerialName("statusHeader")
    val statusHeader: String? = null,

    @SerialName("statusText")
    val statusText: String? = null,

    @SerialName("statusColor")
    val statusColor: String? = null,

    @SerialName("trackingStatusColor")
    val trackingStatusColor: String? = null,

    @SerialName("bgColor")
    val bgColor: String? = null,

    @SerialName("estimatedDeliveryDate")
    val estimatedDeliveryDate: String? = null,

    override val uniqueKey: String = trackingId?.plus(trackingLink)?.plus(statusText)?.plus(statusHeader)?.plus(status).orEmpty()
) : TxnDetailsCardView {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun getSortKey(): Int {
        return BaseConstants.TxnDetailsPosition.TRANSACTION_TRACKING
    }

    override fun equals(other: Any?): Boolean {
        return other is TxnTrackingData
    }

}
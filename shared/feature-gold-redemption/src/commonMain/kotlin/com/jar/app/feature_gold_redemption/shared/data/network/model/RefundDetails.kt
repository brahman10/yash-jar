package com.jar.app.feature_gold_redemption.shared.data.network.model

import com.jar.app.feature_gold_redemption.shared.domain.model.RefundStatus
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class RefundDetails(
    @SerialName("transactionId")
    val transactionId: String? = null,
    @SerialName("refundedOn")
    val refundedOn: String? = null,
    @SerialName("refundedTo")
    val refundedTo: String? = null,
    @SerialName("refundStatus")
    val refundStatus: String? = null,
) : Parcelable {
    fun getRefundStatus(): RefundStatus? {
        if (refundStatus.isNullOrEmpty()) return null
        return RefundStatus.valueOf(refundStatus)
    }
}
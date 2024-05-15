package com.jar.app.feature_gold_lease.shared.domain.model

import com.jar.app.feature_transactions_common.shared.CommonTransactionStatus
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.ColorResource
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class GoldLeaseTransaction(
    @SerialName("iconLink")
    val iconLink: String? = null,

    @SerialName("title")
    val title: String? = null,

    @SerialName("currentStatus")
    val currentStatus: String? = null,

    @SerialName("volume")
    val volume: Float? = null,

    @SerialName("date")
    val date: Long? = null,

    @SerialName("orderId")
    val orderId: String,

    @SerialName("leaseAssetTransactionType")
    val leaseAssetTransactionType: String,

    @SerialName("leaseId")
    val leaseId: String,

    @SerialName("assetSourceType")
    val assetSourceType: String,

    @SerialName("assetTransactionId")
    val assetTransactionId: String
) : Parcelable {

    fun getColorForStatus(): ColorResource {
        val transactionStatus = currentStatus?.uppercase()
        val status: CommonTransactionStatus =
            CommonTransactionStatus.values().find { it.name == transactionStatus }
                ?: CommonTransactionStatus.DEFAULT
        return status.getColor()
    }
}
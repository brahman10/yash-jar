package com.jar.app.feature_gold_lease.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldLeaseV2InitiateRequest(
    @SerialName("assetLeaseConfigId")
    val assetLeaseConfigId: String,

    @SerialName("amountToPay")
    val amountToPay: Float,

    @SerialName("jarVolumeUsed")
    val jarVolumeUsed: Float,

    @SerialName("totalLeaseVolume")
    val totalLeaseVolume: Float,

    @SerialName("emailId")
    val emailId: String,

    @SerialName("panNumber")
    val panNumber: String,

    @SerialName("context")
    val context: String? = null
)

enum class InitiateLeaseContext{
    RETRY
}
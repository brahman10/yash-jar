package com.jar.app.feature_gold_lease.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldLeaseV2InitiateResponse(
    @SerialName("leaseId")
    val leaseId: String? = null,

    @SerialName("pendingVolume")
    val pendingVolume: Float? = null,

    @SerialName("pendingAmount")
    val pendingAmount: Float? = null,

    @SerialName("leaseAssetProvider")
    val leaseAssetProvider: String? = null,

    @SerialName("jewellerId")
    val jewellerId: String? = null
)
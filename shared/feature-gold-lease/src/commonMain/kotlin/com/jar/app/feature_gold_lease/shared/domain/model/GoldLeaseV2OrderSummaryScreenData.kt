package com.jar.app.feature_gold_lease.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldLeaseV2OrderSummaryScreenData(
    @SerialName("leasePlanDetails")
    val leasePlanList: LeasePlanList,

    @SerialName("totalLeasedVolume")
    val totalVolume: Float,

    @SerialName("lockerVolumeUsed")
    val jarVolumeUsed: Float
)
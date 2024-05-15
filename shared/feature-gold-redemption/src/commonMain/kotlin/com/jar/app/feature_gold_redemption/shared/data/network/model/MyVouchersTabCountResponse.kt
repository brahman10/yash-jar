package com.jar.app.feature_gold_redemption.shared.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyVouchersTabCountResponse(
    @SerialName("activeVouchersCount")
    val activeVouchersCount: Int? = null,
    @SerialName("allVouchersCount")
    val allVouchersCount: Int? = null,
)
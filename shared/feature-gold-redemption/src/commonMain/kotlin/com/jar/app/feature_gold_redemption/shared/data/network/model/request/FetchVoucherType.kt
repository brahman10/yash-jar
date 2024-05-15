package com.jar.app.feature_gold_redemption.shared.data.network.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class FetchVoucherType {
    @SerialName("ACTIVE")
    ACTIVE,

    @SerialName("ALL")
    ALL,
}

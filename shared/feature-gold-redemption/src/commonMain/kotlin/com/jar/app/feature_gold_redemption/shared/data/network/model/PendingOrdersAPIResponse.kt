package com.jar.app.feature_gold_redemption.shared.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PendingOrdersAPIResponse(
    @SerialName("desc")
    val desc: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("voucherDetailsForBottomCardList")
    val list: List<PendingOrdersAPIData>? = null,
)
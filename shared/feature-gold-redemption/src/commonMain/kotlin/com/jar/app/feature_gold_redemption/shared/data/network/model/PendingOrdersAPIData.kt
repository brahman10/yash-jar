package com.jar.app.feature_gold_redemption.shared.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PendingOrdersAPIData(
    @SerialName("voucherName")
    val voucherName: String? = null,
    @SerialName("desc")
    val desc: String? = null,
    @SerialName("imageUrl")
    val imageUrl: String? = null,
    @SerialName("orderId")
    val orderId: String? = null,
    @SerialName("voucherOrderId")
    val voucherOrderId: String? = null,
)
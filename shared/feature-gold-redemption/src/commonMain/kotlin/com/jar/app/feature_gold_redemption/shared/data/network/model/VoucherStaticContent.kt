package com.jar.app.feature_gold_redemption.shared.data.network.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VoucherStaticContent(
    @SerialName("imageUrl")
    val imageUrl: String? = null,
    @SerialName("title")
    val title: String? = null
)
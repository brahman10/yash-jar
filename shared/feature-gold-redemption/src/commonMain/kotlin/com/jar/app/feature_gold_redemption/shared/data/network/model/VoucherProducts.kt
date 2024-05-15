package com.jar.app.feature_gold_redemption.shared.data.network.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VoucherProducts(
    @SerialName("discountText")
    val discountText: String? = null,
    @SerialName("goldBonus")
    val goldBonus: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("imageUrl")
    val imageUrl: String? = null,
    @SerialName("startingAmountInfoText")
    val startingAmountText: String? = null,
    @SerialName("title")
    val title: String? = null
)
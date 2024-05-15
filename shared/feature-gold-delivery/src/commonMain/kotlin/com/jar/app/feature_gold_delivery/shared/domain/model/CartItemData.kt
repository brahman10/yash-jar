package com.jar.app.feature_gold_delivery.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class CartItemData(
    @SerialName("amount")
    val amount: Double? = null,

    @SerialName("quantity")
    val quantity: Int? = null,

    @SerialName("label")
    val label: String? = null,

    @SerialName("productId")
    val productId: String? = null,

    @SerialName("volume")
    val volume: Double? = null,

    @SerialName("inStock")
    val inStock: Boolean? = null,

    @SerialName("deliveryMakingCharge")
    val deliveryMakingCharge: Double? = null,

    @SerialName("id")
    val id: String? = null,

    @SerialName("icon")
    val icon: String? = null,

    @SerialName("discountOnTotal")
    val discountOnTotal: Float? = null,

    @SerialName("totalAmount")
    val totalAmount: Double? = null,
) : Parcelable
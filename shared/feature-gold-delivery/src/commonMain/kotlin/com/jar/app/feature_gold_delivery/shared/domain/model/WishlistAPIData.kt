package com.jar.app.feature_gold_delivery.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize

import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class WishlistAPIData(
    @SerialName("id")
    val id: String? = null,
    @SerialName("label")
    val label: String? = null,
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("productId")
    val productId: Int? = null,
    @SerialName("inStock")
    val inStock: Boolean? = null,
    @SerialName("setToNotify")
    val setToNotify: Boolean? = null,
    @SerialName("volume")
    val volume: Float? = null,
    @SerialName("amount")
    val amount: Float? = null,
    @SerialName("discountOnTotal")
    val discountOnTotal: Double? = null
) : Parcelable
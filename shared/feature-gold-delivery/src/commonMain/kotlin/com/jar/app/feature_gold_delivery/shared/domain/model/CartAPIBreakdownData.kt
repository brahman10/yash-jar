package com.jar.app.feature_gold_delivery.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize

import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class CartAPIBreakdownData(
    @SerialName("balanceAmount")
    val balanceAmount: Double? = null,
    @SerialName("cart")
    val cart: List<CartItemData?>? = null,
    @SerialName("jarSavings")
    val jarSavings: Double? = null,
    @SerialName("jarSavingsInGm")
    val jarSavingsInGm: Double? = null,
    @SerialName("netAmount")
    val netAmount: Double? = null,
    @SerialName("netAmountWithJarSavingsUsed")
    val netAmountWithJarSavingsUsed: Double? = null,
    @SerialName("totalDeliveryCharge")
    val totalDeliveryCharge: Double? = null,
    @SerialName("totalVolume")
    val totalVolume: Double? = null
) : Parcelable
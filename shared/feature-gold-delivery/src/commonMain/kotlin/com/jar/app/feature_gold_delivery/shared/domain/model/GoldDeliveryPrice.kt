package com.jar.app.feature_gold_delivery.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class GoldDeliveryPrice(
    @SerialName("deliveryMakingCharge")
    val deliveryMakingCharge: Double,

    @SerialName("gold")
    val gold: Double,

    @SerialName("total")
    val total: Double,

    @SerialName("jarSavings")
    val jarSavings: Double? = null,

    @SerialName("payableAmount")
    val payableAmount: Double? = null,

    @SerialName("isDeliveryChargeEnabled")
    val isDeliveryChargeEnabled: Boolean,

    @SerialName("discountOnProduct")
    val discountOnProduct: Double? = null,

    @SerialName("discountOnCharges")
    val discountOnCharges: Double? = null,

    @SerialName("discountOnTotal")
    val discountOnTotal: Double? = null,
) : Parcelable
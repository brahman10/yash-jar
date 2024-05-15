package com.jar.app.feature_gold_redemption.shared.data.network.model.request

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class GoldRedemptionInitiateCreateOrderRequest(
    @SerialName("amount")
    val amount: Float? = null,
    @SerialName("productId")
    val productId: String? = null,
    @SerialName("quantity")
    val quantity: Int? = null,
) : Parcelable
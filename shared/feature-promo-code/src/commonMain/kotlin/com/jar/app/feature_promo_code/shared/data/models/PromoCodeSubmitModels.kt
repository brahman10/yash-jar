package com.jar.app.feature_promo_code.shared.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PromoCodeSubmitRequest (
    @SerialName("promoCode")
    val promoCode: String
)

@Serializable
data class PromoCodeSubmitResponse (
    @SerialName("orderId")
    val orderId: String
)
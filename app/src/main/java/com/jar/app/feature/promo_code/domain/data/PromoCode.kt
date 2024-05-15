package com.jar.app.feature.promo_code.domain.data

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class PromoCode(
    @SerialName("id")
    val id: String,
    @SerialName("promoCode")
    val promoCode: String,
    @SerialName("description")
    val description: String,
    @SerialName("active")
    val isActive: Boolean,
    @SerialName("expiresOn")
    val expiresOn: Long? = null
)

enum class PromoCodeType{
    USER_PROMO_CODE
}
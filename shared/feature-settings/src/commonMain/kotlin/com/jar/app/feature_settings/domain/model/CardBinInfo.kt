package com.jar.app.feature_settings.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class CardBinInfo(
    @SerialName("id")
    val id: String? = null,
    @SerialName("cardSubType")
    val cardSubType: String? = null,
    @SerialName("brand")
    val cardBrand: String? = null,
    @SerialName("bank")
    val cardBank: String? = null,
    @SerialName("type")
    val cardType: String? = null,
    @SerialName("juspayBankCode")
    val juspayBankCode: String? = null,
    @SerialName("country")
    val country: String? = null
)

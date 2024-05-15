package com.jar.app.feature_homepage.shared.domain.model.user_gold_breakdown

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UserGoldBreakdownResponse(
    @SerialName("keys")
    val keys: List<String>? = null,

    @SerialName("values")
    val values: List<Float>,

    @SerialName("goldValues")
    val goldValues: List<Float>,

    @SerialName("totalAmount")
    val totalAmount: Float? = null,

    @SerialName("aggrBuyPrice")
    val aggrBuyPrice: Float? = null,

    @SerialName("unitPreference")
    val unitPreference: String? = null,

    val userGoldBreakdownList: List<UserGoldBreakdown>? = null
)
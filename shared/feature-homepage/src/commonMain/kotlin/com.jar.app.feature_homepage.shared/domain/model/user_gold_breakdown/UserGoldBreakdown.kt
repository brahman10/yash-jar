package com.jar.app.feature_homepage.shared.domain.model.user_gold_breakdown

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UserGoldBreakdown(
    @SerialName("key")
    val key: String,

    @SerialName("value")
    val value: String
)
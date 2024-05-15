package com.jar.app.feature_gold_sip.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldSipData(
    @SerialName("icon")
    val iconUrl: String,
    @SerialName("text")
    val title: String
)
package com.jar.feature_gold_price_alerts.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TableData(
    @SerialName("iconUrl")
    val iconUrl: String? = null,
    @SerialName("key")
    val key: String? = null,
    @SerialName("value")
    val value: String? = null,
)
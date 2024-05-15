package com.jar.feature_gold_price_alerts.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class LatestGoldPriceAlertResponse(
    @SerialName("alertId")
    val alertId: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("imageUrl")
    val imageUrl: String? = null,
    @SerialName("removeAlertCta")
    val removeAlertCta: RemoveAlertCta? = null,
    @SerialName("tableData")
    val tableData: List<TableData>? = null,
    @SerialName("title")
    val title: String? = null
)

@kotlinx.serialization.Serializable
data class RemoveAlertCta(
    @SerialName("isEnabled")
    val isEnabled: Boolean? = null,
    @SerialName("title")
    val title: String? = null
)
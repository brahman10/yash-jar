package com.jar.feature_gold_price_alerts.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class SavingsCard(
    @SerialName("saveGoldCta")
    val saveGoldCta: SaveGoldCta? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("footerIconUrl")
    val footerIconUrl: String? = null,
    @SerialName("footerText")
    val footerText: String? = null,
    @SerialName("imageUrl")
    val imageUrl: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("footerTable")
    val footerTable: List<SavingsCardFooterTable>? = null,
    //Only used for analytics as of now
    val cardType: String? = null
)

@Serializable
data class SavingsCardFooterTable(
    @SerialName("value")
    val value: String,
    @SerialName("iconUrl")
    val iconUrl: String
)
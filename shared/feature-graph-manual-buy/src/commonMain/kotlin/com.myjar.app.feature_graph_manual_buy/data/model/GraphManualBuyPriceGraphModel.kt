package com.myjar.app.feature_graph_manual_buy.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GraphManualBuyPriceGraphModel(
    @SerialName("title")
    val title: String? = null,

    @SerialName("faqIcon")
    val faqIcon: String? = null,

    @SerialName("trendsStartText")
    val trendsStartText: String? = null,

    @SerialName("trendsEndText")
    val trendsEndText: String? = null,

    @SerialName("trendsEndAmount")
    val trendsEndAmount: String? = null,

    @SerialName("trendsMidText")
    val trendsMidText: String? = null,

    @SerialName("midGraphAmount")
    val midGraphAmount: String? = null,

    @SerialName("totalManualSavings")
    val totalManualSavings: KeyValueData? = null,

    @SerialName("expectedGrowth")
    val expectedGrowth: KeyValueData? = null,

    @SerialName("buyGoldCta")
    val buyGoldCta: BuyGoldCta? = null,

    @SerialName("yaxis")
    val yaxis: List<String>? = null,

    @SerialName("xaxis")
    val xaxis: List<String>? = null
)

@Serializable
data class KeyValueData(
    @SerialName("key")
    val key: String? = null,

    @SerialName("value")
    val value: String? = null,

    @SerialName("copy")
    val copy: Boolean? = null
)

@Serializable
data class BuyGoldCta(
    @SerialName("text")
    val text: String? = null,

    @SerialName("iconLink")
    val iconLink: String? = null,

    @SerialName("deeplink")
    val deeplink: String? = null
)

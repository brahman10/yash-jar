package com.jar.app.feature_daily_investment_cancellation.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DailyInvestmentConfirmActionDetails(
    @SerialName("header")
    val header: String? = null,

    @SerialName("continueButtonText")
    val continueButtonText: String? = null,

    @SerialName("stopButtonText")
    val stopButtonText: String? = null,

    @SerialName("statisticsContent")
    val statisticsContent: StatisticsContent? = null,

    @SerialName("knowledgeContent")
    val knowledgeContent: KnowledgeContent? = null,
)

@kotlinx.serialization.Serializable
data class StatisticsContent (
    @SerialName("title")
    val title: String? = null,

    @SerialName("rating")
    val rating: String? = null,

    @SerialName("downloads")
    val downloads: String? = null,
)


@kotlinx.serialization.Serializable
data class KnowledgeContent (
    @SerialName("title")
    val title: String? = null,

    @SerialName("features")
    val features: Map<Int?,String?>? = null,

    @SerialName("footer")
    val footer: String? = null,
)

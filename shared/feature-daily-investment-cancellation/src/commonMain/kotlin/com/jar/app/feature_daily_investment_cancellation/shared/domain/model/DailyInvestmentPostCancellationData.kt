package com.jar.app.feature_daily_investment_cancellation.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DailyInvestmentPostCancellationData(
    @SerialName("headerText")
    val headerText: String? = null,

    @SerialName("buyGoldText")
    val buyGoldText: String? = null,

    @SerialName("statisticsText")
    val statisticsText: String? = null,

    @SerialName("saveButtonText")
    val saveButtonText: String? = null,

    @SerialName("buyButtonText")
    val buyButtonText: String? = null,

    @SerialName("footerText")
    val footerText: String? = null
)

package com.jar.app.feature_daily_investment.shared.domain.model

import kotlinx.serialization.SerialName


@kotlinx.serialization.Serializable
data class DailyInvestmentOnboardingFragmentData(
    @SerialName("headerImageUrl")
    val headerImageUrl: String? = null,

    @SerialName("savingText")
    val ETText: String? = null,

    @SerialName("ctaText")
    val ctaText: String? = null,

    @SerialName("footerImageUrl")
    val footerImageUrl: String? = null
)

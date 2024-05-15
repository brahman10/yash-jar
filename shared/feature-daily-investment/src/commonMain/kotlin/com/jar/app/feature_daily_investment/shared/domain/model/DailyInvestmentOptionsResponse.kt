package com.jar.app.feature_daily_investment.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DailyInvestmentOptionsResponse(
    @SerialName("recurringSavings")
    val recurringSavings: RecurringOptionData

)
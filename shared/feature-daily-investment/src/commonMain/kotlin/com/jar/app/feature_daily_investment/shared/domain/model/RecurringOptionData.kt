package com.jar.app.feature_daily_investment.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class RecurringOptionData(
    @SerialName("maxAmt")
    val maxAmt: Float,

    @SerialName("minAmt")
    val minAmt: Float,

    @SerialName("amount")
    val amount: Float,

    @SerialName("options")
    val options: List<SuggestedRecurringAmount>,

    @SerialName("recurringEnabled")
    val recurringEnabled: Boolean,

    @SerialName("maxMandateAmount")
    val maxMandateAmount: Float?,
)
package com.jar.app.feature_daily_investment.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DailyInvestmentMandateBottomSheetData(
    @SerialName("staticContent")
    val staticContent: StaticContent? = null,

    @SerialName("mandates")
    val mandates: List<Mandates>? = null,
)

@kotlinx.serialization.Serializable
data class StaticContent(
    @SerialName("headerText")
    val headerText: String? = null,

    @SerialName("paymentMethodText")
    val paymentMethodText: String? = null,

    @SerialName("buttonText")
    val buttonText: String? = null,

    @SerialName("footerText")
    val footerText: String? = null,
)

@kotlinx.serialization.Serializable
data class Mandates(
    @SerialName("enabled")
    val status: String? = null,

    @SerialName("amountText")
    val amountText: String? = null,

    @SerialName("typeText")
    val typeText: String? = null,
)



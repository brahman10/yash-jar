package com.jar.app.feature_lending.shared.domain.model.creditReport

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CreditReport(
    @SerialName("creditReportExist") val creditReportExist: Boolean? = null,
    @SerialName("reportDetails") val reportDetails: ReportDetails? = null,
    @SerialName("creditInformation") val creditInformation: CreditInformation? = null

)
@Serializable
data class RealTimeBenefits(
    @SerialName("imageUrl") val imageUrl: String? = null,
    @SerialName("description") val description: String? = null
)
@Serializable
data class CreditInformation(
    @SerialName("title") val title: String? = null,
    @SerialName("realTimeBenefits") val realTimeBenefits: List<RealTimeBenefits>? = null,
)
@Serializable
data class ReportDetails(
    @SerialName("creditScore") val creditScore: Int? = null,
    @SerialName("creditScoreResult") val creditScoreResult: String? = null,
    @SerialName("footerText") val footerText: String? = null
)
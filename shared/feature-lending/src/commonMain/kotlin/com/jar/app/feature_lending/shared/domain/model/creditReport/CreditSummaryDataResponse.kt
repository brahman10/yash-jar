package com.jar.app.feature_lending.shared.domain.model.creditReport
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class RefreshCreditSummaryDataResponse(
    @SerialName("success")
    val success: Boolean? = null
)
@Serializable
data class CreditSummaryDataResponse(
    @SerialName("creditScore")
    val creditScore: Int? = null,
    @SerialName("creditScoreResult")
    val creditScoreResult: String? = null,
    @SerialName("performance")
    val performance: List<Performance>? = null,
    @SerialName("refreshCreditReport")
    val refreshCreditReport: RefreshCreditReport? = null,
    @SerialName("impactFactors")
    val impactFactors: ImpactFactors? = null
)
@Serializable
data class Factors(
    @SerialName("name") val name: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("icon") val icon: String? = null,
    @SerialName("description") val description: String? = null
)
@Serializable
data class ImpactFactors(
    @SerialName("title")
    val title: String? = null,
    @SerialName("factors")
    val factors: List<Factors>? = null
)
@Parcelize
@Serializable
data class Performance(
    @SerialName("type") val type: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("impact") val impact: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("statusColor") val statusColor: String? = null

):Parcelable
@Serializable
data class RefreshCreditReport(
    @SerialName("refreshCreditScore") val refreshCreditScore: Boolean? = null,
    @SerialName("lastUpdated") val lastUpdated: String? = null
)
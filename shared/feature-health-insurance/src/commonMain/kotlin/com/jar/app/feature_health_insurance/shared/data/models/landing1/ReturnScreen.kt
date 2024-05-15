package com.jar.app.feature_health_insurance.shared.data.models.landing1


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReturnScreen(
    @SerialName("benefitsList")
    val benefitsList: List<BenefitsX>?,
    @SerialName("comparisonFieldsList")
    val comparisonFieldsList: List<ComparisonFields>?,
    @SerialName("exitCTA")
    val exitCTA: String?,
    @SerialName("exitCTAText")
    val exitCTAText: String?,
    @SerialName("getQuoteCTA")
    val getQuoteCTA: String?,
    @SerialName("getQuoteCTAText")
    val getQuoteCTAText: String?,
    @SerialName("headerText")
    val headerText: String?,
    @SerialName("subText")
    val subText: String?
)
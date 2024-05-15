package com.jar.app.feature_health_insurance.shared.data.models.landing

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BenefitsV2(
    val benefitsCTALink: String,
    val benefitsCTAText: String,
    @SerialName("benefitsCTAIcon")
    val benefitsCTAIcon: String?,
    val benefitsDetailsList: List<BenefitsDetailsV2>? = null,
    val headerText: String
)

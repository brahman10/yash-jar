package com.jar.app.feature_health_insurance.shared.data.models.landing

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Benefits(
    @SerialName("benefitsCTALink")
    val benefitsCTALink: String,
    @SerialName("benefitsCTAText")
    val benefitsCTAText: String,
    @SerialName("benefitsCTAIcon")
    val benefitsCTAIcon: String?,
    @SerialName("benefitsDetailsList")
    val benefitsDetailsList: List<BenefitsDetails>? = null,
    @SerialName("headerText")
    val headerText: String
)
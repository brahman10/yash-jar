package com.jar.app.feature_health_insurance.shared.data.models.landing1


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Benefits(
    @SerialName("benefitsCTALink")
    val benefitsCTALink: String?,
    @SerialName("benefitsCTAText")
    val benefitsCTAText: String?,
    @SerialName("benefitsDetailsList")
    val benefitsDetailsList: List<BenefitsDetails?>?,
    @SerialName("headerText")
    val headerText: String?
)
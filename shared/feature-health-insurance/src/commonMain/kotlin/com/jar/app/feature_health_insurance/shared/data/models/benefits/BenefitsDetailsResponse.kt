package com.jar.app.feature_health_insurance.shared.data.models.benefits

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class BenefitsDetailsResponse(
    @SerialName("benefitsResponseList")
    val benefitsList: List<Benefit>,
    @SerialName("toolBarText")
    val toolBarText: String
)
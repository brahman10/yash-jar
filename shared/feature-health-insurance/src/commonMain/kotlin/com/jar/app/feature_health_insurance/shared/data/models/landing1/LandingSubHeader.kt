package com.jar.app.feature_health_insurance.shared.data.models.landing1


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LandingSubHeader(
    @SerialName("headerText")
    val headerText: String?,
    @SerialName("hiSubHeaderInsuranceBenefitsList")
    val hiSubHeaderInsuranceBenefitsList: List<HiSubHeaderInsuranceBenefits?>?
)
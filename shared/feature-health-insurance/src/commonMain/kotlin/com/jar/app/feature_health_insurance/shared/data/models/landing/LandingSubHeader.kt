package com.jar.app.feature_health_insurance.shared.data.models.landing

import kotlinx.serialization.Serializable

@Serializable
data class LandingSubHeader(
    val headerText: String?=null,
    val hiSubHeaderInsuranceBenefitsList: List<HiSubHeaderInsuranceBenefits>?=null
)
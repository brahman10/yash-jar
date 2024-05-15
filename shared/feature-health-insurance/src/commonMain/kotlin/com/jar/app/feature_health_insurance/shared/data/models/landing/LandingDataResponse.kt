package com.jar.app.feature_health_insurance.shared.data.models.landing

import kotlinx.serialization.Serializable

@Serializable
data class LandingDataResponse(
    val benefits: Benefits?=null,
    val hospitals: Hospitals?=null,
    val landingFooter: LandingFooter?=null,
    val landingFooterCTA: LandingFooterCTA?=null,
    val landingHeader: LandingHeader?=null,
    val landingSubHeader: LandingSubHeader?=null,
    val partnerships: Partnerships?=null,
    val returnScreen: ReturnScreen?=null
)
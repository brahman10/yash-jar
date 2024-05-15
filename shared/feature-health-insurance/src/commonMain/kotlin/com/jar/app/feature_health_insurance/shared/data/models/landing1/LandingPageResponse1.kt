package com.jar.app.feature_health_insurance.shared.data.models.landing1


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LandingPageResponse1(
    @SerialName("benefits")
    val benefits: Benefits? = null,
    @SerialName("jarBenefits")
    val jarBenefits: Benefits? = null,
    @SerialName("hospitals")
    val hospitals: Hospitals? = null,
    @SerialName("landingFooter")
    val landingFooter: LandingFooter? = null,
    @SerialName("landingFooterCTA")
    val landingFooterCTA: LandingFooterCTA? = null,
    @SerialName("landingHeader")
    val landingHeader: LandingHeader? = null,
    @SerialName("landingSubHeader")
    val landingSubHeader: LandingSubHeader? = null,
    @SerialName("partnerships")
    val partnerships: Partnerships? = null,
    @SerialName("returnScreen")
    val returnScreen: ReturnScreen? = null,
    @SerialName("landingVideo")
    val landingVideoSection: LandingVideoSection? = null
)
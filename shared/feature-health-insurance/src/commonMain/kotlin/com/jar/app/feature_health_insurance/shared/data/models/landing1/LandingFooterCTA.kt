package com.jar.app.feature_health_insurance.shared.data.models.landing1


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LandingFooterCTA(
    @SerialName("ctaDeeplink")
    val ctaDeeplink: String?,
    @SerialName("ctaText")
    val ctaText: String?,
    @SerialName("iciciIcon")
    val iciciIcon: String?,
    @SerialName("inPartnershipText")
    val inPartnershipText: String?
)
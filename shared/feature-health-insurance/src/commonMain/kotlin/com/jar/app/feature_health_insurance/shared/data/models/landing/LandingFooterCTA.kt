package com.jar.app.feature_health_insurance.shared.data.models.landing

import kotlinx.serialization.Serializable

@Serializable
data class LandingFooterCTA(
    val ctaDeeplink: String?=null,
    val ctaText: String?=null,
    val iciciIcon: String?=null,
    val inPartnershipText: String?=null
)
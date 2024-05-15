package com.jar.app.feature_health_insurance.shared.data.models.landing

import kotlinx.serialization.Serializable

@Serializable
data class LandingFooter(
    val headerText: String?=null,
    val icon: String?=null,
    val subHeaderText: String?=null
)
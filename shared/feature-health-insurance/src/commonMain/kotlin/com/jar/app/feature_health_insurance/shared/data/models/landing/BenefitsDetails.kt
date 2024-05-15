package com.jar.app.feature_health_insurance.shared.data.models.landing

import kotlinx.serialization.Serializable

@Serializable
data class BenefitsDetails(
    val icon: String?=null,
    val text: String,
    val activeIcon: String? = null,
    val inActiveIcon: String? = null
)
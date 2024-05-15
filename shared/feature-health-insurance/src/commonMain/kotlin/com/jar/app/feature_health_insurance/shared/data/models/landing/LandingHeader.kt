package com.jar.app.feature_health_insurance.shared.data.models.landing

import kotlinx.serialization.Serializable

@Serializable
data class LandingHeader(
    val familyImage: String?=null,
    val faqIcon: String?=null,
    val faqText: String?=null,
    val header: String?=null,
    val infoIcon: String?=null,
    val infoText: String?=null,
    val subHeader: String?=null
)
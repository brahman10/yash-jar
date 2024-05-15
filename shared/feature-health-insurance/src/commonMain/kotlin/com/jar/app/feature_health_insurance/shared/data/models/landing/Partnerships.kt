package com.jar.app.feature_health_insurance.shared.data.models.landing

import kotlinx.serialization.Serializable

@Serializable
data class Partnerships(
    val headerText: String?=null,
    val partnershipCardsList: List<PartnershipCards>?=null
)
package com.jar.app.feature_health_insurance.shared.data.models.landing

import kotlinx.serialization.Serializable

@Serializable
data class PartnershipCards(
    val header: String?=null,
    val icon: String?=null,
    val subHeader: String?=null
)
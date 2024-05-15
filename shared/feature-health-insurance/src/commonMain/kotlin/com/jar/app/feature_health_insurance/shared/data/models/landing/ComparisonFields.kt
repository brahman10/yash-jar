package com.jar.app.feature_health_insurance.shared.data.models.landing

import kotlinx.serialization.Serializable

@Serializable
data class ComparisonFields(
    val image: String?=null,
    val name: String?=null,
    val value: String?=null
)
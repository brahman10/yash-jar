package com.jar.app.feature_health_insurance.shared.data.models.landing

import kotlinx.serialization.Serializable

@Serializable
data class Hospitals(
    val headerText: String?=null,
    val hospitalList: List<String>?=null
)
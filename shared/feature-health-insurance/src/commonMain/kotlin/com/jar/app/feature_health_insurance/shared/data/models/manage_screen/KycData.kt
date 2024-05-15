package com.jar.app.feature_health_insurance.shared.data.models.manage_screen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class KycData(
    @SerialName("header") val header: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("cta") val cta: InsuranceCTA? = null,
)

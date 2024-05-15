package com.jar.app.feature_health_insurance.shared.data.models.manage_screen


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Communication(
    @SerialName("cta")
    val cta: InsuranceCTA?,
    @SerialName("label")
    val label: String?,
    @SerialName("labelIcon")
    val labelIcon: String?,
    @SerialName("value")
    val value: String?
)
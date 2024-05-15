package com.jar.app.feature_health_insurance.shared.data.models.manage_screen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsuranceStatusDetails(
    @SerialName("icon") val icon: String? = null,
    @SerialName("header") val header: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("descriptionColor") val descriptionColor: String? = null,
    @SerialName("cta") val cta: List<InsuranceCTA>? = null,
    @SerialName("notification") val notification: NotificationCard? = null,
)

package com.jar.app.feature_health_insurance.shared.data.models.manage_screen

import com.jar.app.feature_health_insurance.shared.data.models.benefits.Benefit
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsuranceDataSection(
    @SerialName("title") val title: String? = null,
    @SerialName("titleIcon") val titleIcon: String? = null,
    @SerialName("benefits") val benefits: List<Benefit>? = null,
    @SerialName("card") val card: InsuranceCardData? = null,
    @SerialName("cta") val cta: InsuranceCTA? = null,
    @SerialName("notification") val notification: NotificationCard? = null,
    @SerialName("kyc") val kyc: KycData? = null,
    @SerialName("sectionType") val sectionType: String
)
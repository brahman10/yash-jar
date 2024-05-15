package com.jar.app.feature_health_insurance.shared.data.models.payment_status

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Attribute(
    @SerialName("label")
    val label: String? = null,
    @SerialName("value")
    val value: String? = null,
    @SerialName("valueIcon")
    val valueIcon: String? = null,
    @SerialName("labelColor")
    val labelColor: String? = null,
    @SerialName("valueColor")
    val valueColor: String? = null,
    @SerialName("bold")
    val bold: Boolean? = null,
    @SerialName("valueTruncate")
    val valueTruncate: Boolean? = null
)

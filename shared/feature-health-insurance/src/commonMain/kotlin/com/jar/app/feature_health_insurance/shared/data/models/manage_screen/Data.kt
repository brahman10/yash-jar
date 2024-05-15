package com.jar.app.feature_health_insurance.shared.data.models.manage_screen


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    @SerialName("bold")
    val bold: Boolean?,
    @SerialName("label")
    val label: String?,
    @SerialName("value")
    val value: String?,
    @SerialName("valueIcon")
    val valueIcon: String?
)
package com.jar.app.feature_health_insurance.shared.data.models.manage_screen


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NeedHelp(
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("text")
    val text: String?,
    @SerialName("whatsappNumber")
    val whatsappNumber: String?,
    @SerialName("whatsappText")
    val whatsappText: String?
)
package com.jar.app.feature_health_insurance.shared.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NeedHelp(
    @SerialName("text")
    val text: String?,
    @SerialName("icon")
    val icon: String?,
    @SerialName("whatsappNumber")
    val whatsappNumber: String?,
    @SerialName("whatsappText")
    val whatsappText: String?,
)

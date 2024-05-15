package com.jar.app.feature_health_insurance.shared.data.models.manage_screen


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ViewBenefits(
    @SerialName("icon")
    val icon: String?,
    @SerialName("link")
    val link: String?,
    @SerialName("text")
    val text: String
)
package com.jar.app.feature_health_insurance.shared.data.models.manage_screen


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Point(
    @SerialName("description")
    val description: String?,
    @SerialName("id")
    val id: String?,
    @SerialName("title")
    val title: String?
)
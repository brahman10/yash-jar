package com.jar.app.feature_health_insurance.shared.data.models.landing1


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ComparisonFields(
    @SerialName("image")
    val image: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("value")
    val value: String?
)
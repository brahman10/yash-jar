package com.jar.app.feature_health_insurance.shared.data.models.landing1


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Hospitals(
    @SerialName("headerText")
    val headerText: String?,
    @SerialName("hospitalList")
    val hospitalList: List<String?>?
)
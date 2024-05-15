package com.jar.app.feature_health_insurance.shared.data.models.manage_screen


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PolicyCard(
    @SerialName("dob")
    val dob: String?,
    @SerialName("insuranceType")
    val insuranceType: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("policyNoText")
    val policyNoText: String?,
    @SerialName("policyNoValue")
    val policyNoValue: String?,
    @SerialName("providerIcon")
    val providerIcon: String?,
    @SerialName("validity")
    val validity: String?,
    @SerialName("validityText")
    val validityText: String?
)
package com.jar.app.feature_lending.shared.domain.model.v2


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmploymentDetailsBody(
    @SerialName("companyName")
    val companyName: String? = null,
    @SerialName("employmentType")
    val employmentType: String? = null,
    @SerialName("monthlyIncome")
    val monthlyIncome: Float? = null
)
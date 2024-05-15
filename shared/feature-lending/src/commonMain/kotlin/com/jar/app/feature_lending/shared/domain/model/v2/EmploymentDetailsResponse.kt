package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class EmploymentDetailsResponse(
    @SerialName("annualIncome")
    val annualIncome: Float? = null,
    @SerialName("companyName")
    val companyName: String? = null,
    @SerialName("createdAtEpoch")
    val createdAtEpoch: Long? = null,
    @SerialName("designation")
    val designation: String? = null,
    @SerialName("employmentType")
    val employmentType: String? = null,
    @SerialName("monthlyIncome")
    val monthlyIncome: Float? = null,
    @SerialName("occupation")
    val occupation: String? = null,
    @SerialName("status")
    val status: String? = null
)
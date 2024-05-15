package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Employment(
    @SerialName("companyName")
    val companyName: String? = null,
    @SerialName("createdAtEpoch")
    val createdAtEpoch: Long? = null,
    @SerialName("employmentType")
    val employmentType: String? = null,
    @SerialName("monthlyIncome")
    val monthlyIncome: Float? = null,
    @SerialName("status")
    val status: String? = null
)
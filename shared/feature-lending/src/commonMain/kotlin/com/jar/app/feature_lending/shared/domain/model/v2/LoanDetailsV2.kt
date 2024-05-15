package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class LoanDetailsV2(
    @SerialName("applicationDetails")
    val applicationDetails: ApplicationDetails? = null,
    @SerialName("applicationId")
    val applicationId: String? = null,
    @SerialName("lender")
    val lender: String? = null,
    @SerialName("pinCode")
    val pinCode: Int? = null,
    @SerialName("status")
    val status: String? = null
)
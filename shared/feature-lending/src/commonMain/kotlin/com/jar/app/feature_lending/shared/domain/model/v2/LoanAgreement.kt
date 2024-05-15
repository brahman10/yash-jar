package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class LoanAgreement(
    @SerialName("consent")
    val consent: List<String>? = null,
    @SerialName("instructions")
    val instructions: List<String>? = null
)
package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class LendingEligibilityRange(
    @SerialName("loanAmount")
    val loanAmount: String,
    @SerialName("interestRate")
    val interestRate: String,
    @SerialName("tenure")
    val tenure: String
)
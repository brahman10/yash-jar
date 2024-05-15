package com.jar.app.feature_lending.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SendLendingOtpResponse(
    @SerialName("attemptsRemaining")
    val attemptsRemaining: Int? = null,

    @SerialName("validityInSeconds")
    val validityInSeconds: Long? = null
)
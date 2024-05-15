package com.jar.app.feature_lending_kyc.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class VerifyAadhaarOtpRequest(
    @SerialName("otp")
    val otp: String,

    @SerialName("sessionId")
    val sessionId: String
)
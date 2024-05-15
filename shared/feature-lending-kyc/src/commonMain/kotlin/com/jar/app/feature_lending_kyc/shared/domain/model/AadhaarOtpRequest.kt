package com.jar.app.feature_lending_kyc.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AadhaarOtpRequest(
    @SerialName("aadhaar")
    val aadhaar: String,

    @SerialName("securityCode")
    val securityCode: String,

    @SerialName("sessionId")
    val sessionId: String
)
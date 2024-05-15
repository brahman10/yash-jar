package com.jar.app.feature_lending_kyc.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class CreditReportOtpResponseV2(
    @SerialName("attemptLeft")
    val attemptLeft: Int? = null,
    @SerialName("mobileNumber")
    val mobileNumber: String? = null,
    @SerialName("resentOTPInSeconds")
    val resentOTPInSeconds: Int? = null,
    @SerialName("validityInSeconds")
    val validityInSeconds: Int? = null
)

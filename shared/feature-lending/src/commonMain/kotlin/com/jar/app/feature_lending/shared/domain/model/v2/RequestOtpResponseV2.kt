package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class RequestOtpResponseV2(
    @SerialName("attemptLeft")
    val attemptLeft: Int? = null,
    @SerialName("mobileNumber")
    val mobileNumber: String? = null,
    @SerialName("resentOTPInSeconds")
    val resentOTPInSeconds: Int? = null,
    @SerialName("validityInSeconds")
    val validityInSeconds: Int? = null
)
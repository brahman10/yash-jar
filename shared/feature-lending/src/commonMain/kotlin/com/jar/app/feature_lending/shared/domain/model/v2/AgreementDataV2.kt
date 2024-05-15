package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AgreementDataV2(
    @SerialName("agreementId")
    val agreementId: String? = null,
    @SerialName("agreementLink")
    val agreementLink: String? = null,
    @SerialName("consent")
    val consent: String? = null,   //HTML
    @SerialName("otp")
    val otp: String? = null,
    @SerialName("otpAttemptsRemaining")
    val otpAttemptsRemaining: Int? = null,
    @SerialName("otpRequestAt")
    val otpRequestAt: String? = null,
    @SerialName("otpStatus")
    val otpStatus: String? = null,
    @SerialName("otpValidity")
    val otpValidity: Int? = null,
    @SerialName("provider")
    val provider: String? = null,
    @SerialName("resendTime")
    val resendTime: Int? = null,
    @SerialName("signedAtEpoch")
    val signedAtEpoch: Long? = null,
    @SerialName("status")
    val status: String? = null
)
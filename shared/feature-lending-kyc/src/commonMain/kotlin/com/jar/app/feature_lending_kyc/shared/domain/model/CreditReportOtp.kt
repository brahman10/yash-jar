package com.jar.app.feature_lending_kyc.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class CreditReportOtp(
    @SerialName("code")
    val code: String? = null,

    @SerialName("jarVerifiedPAN")
    val jarVerifiedPAN: Boolean?=null,

    @SerialName("message")
    val message: String? = null,

    @SerialName("stgOneHitId")
    val stgOneHitId: String? = null,

    @SerialName("stgTwoHitId")
    val stgTwoHitId: String? = null,

    @SerialName("creditReportPAN")
    val creditReportPAN: CreditReportPAN? = null,

    @SerialName("attemptLeft")
    val attemptLeft: Int? = null,

    @SerialName("resentOTPInSeconds")
    val resentOTPInSeconds: Long? = null,

    @SerialName("validityInSeconds")
    val validityInSeconds: Long? = null,
)
package com.jar.app.feature_lending_kyc.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class CreditReportOtpRequest(
    @SerialName("otp")
    val otp: String,

    @SerialName("stgOneHitId")
    val stgOneHitId: String,

    @SerialName("stgTwoHitId")
    val stgTwoHitId: String,
)
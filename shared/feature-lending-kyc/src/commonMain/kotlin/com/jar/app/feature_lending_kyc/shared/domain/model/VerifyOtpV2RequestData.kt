package com.jar.app.feature_lending_kyc.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyOtpV2RequestData(
    @SerialName("name")
    val name: String?=null,
    @SerialName("otp")
    val otp: String,
    @SerialName("type")
    val type: String = "EXPERIAN",
    @SerialName("ipAddress")
    val ipAddress: String? = null,
    @SerialName("panNumber")
    val panNumber: String? = null
)

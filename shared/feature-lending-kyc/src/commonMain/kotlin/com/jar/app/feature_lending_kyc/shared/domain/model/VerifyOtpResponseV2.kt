package com.jar.app.feature_lending_kyc.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class VerifyOtpResponseV2(
    @SerialName("success")
    val success:Boolean?=null
)

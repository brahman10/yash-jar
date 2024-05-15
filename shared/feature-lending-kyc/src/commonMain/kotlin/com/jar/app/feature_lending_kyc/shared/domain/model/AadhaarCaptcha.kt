package com.jar.app.feature_lending_kyc.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AadhaarCaptcha(
    @SerialName("captchaImage")
    val captchaImage: String? = null,

    @SerialName("sessionId")
    val sessionId: String? = null
)
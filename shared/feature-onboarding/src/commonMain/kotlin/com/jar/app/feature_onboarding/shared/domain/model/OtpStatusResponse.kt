package com.jar.app.feature_onboarding.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class OtpStatusResponse(
    @SerialName("status")
    val otpStatus: String,
    @SerialName("shouldShowWhatsappCta")
    val shouldShowWhatsappCta: Boolean,
    @SerialName("message")
    val message: String? = null,
    @SerialName("whatsappNumber")
    val whatsappNumber: String,
)
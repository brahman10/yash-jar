package com.jar.app.feature_onboarding.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExperianTCResponse(
    @SerialName("contentType")
    val contentType: String,
    @SerialName("experianTnC")
    val experianConsent: ExperianConsentResponse,
)

@Serializable
data class ExperianConsentResponse(
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
)

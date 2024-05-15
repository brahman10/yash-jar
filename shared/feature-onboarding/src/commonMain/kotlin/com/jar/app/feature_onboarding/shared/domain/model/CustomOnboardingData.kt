package com.jar.app.feature_onboarding.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CustomOnboardingData(
    val customOnboardingLink: String? = null,

    val version: String? = null,

    val infographicType: String? = null,

    val infographicLink: String? = null,
)
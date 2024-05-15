package com.jar.app.feature_onboarding.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EligibleMandateApps(
    @SerialName("eligibleMandateApps")
    val eligibleMandateAppsList: List<String>
)
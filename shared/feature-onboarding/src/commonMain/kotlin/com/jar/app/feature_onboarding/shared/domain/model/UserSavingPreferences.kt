package com.jar.app.feature_onboarding.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UserSavingPreferences(
    @SerialName("savingsGoals")
    val savingsGoals: List<com.jar.app.feature_onboarding.shared.domain.model.ReasonForSavings>? = null,
    @SerialName("preferences")
    val preferences: com.jar.app.feature_onboarding.shared.domain.model.SavingsFrequencyPreference? = null
)

@kotlinx.serialization.Serializable
data class SavingsFrequencyPreference(
    @SerialName("savingsFrequency")
    val savingsFrequency: String? = null
)
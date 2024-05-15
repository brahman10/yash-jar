package com.jar.app.feature_onboarding.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SavingGoalPostRequest(
    @SerialName("savingsGoals")
    val savingsGoals: List<String>
)
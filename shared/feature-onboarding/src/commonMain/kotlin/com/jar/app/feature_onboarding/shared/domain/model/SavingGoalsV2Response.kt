package com.jar.app.feature_onboarding.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SavingGoalsV2Response(
    @SerialName("contentType")
    val contentType: String,
    @SerialName("savingsGoalRevampResponse")
    val savingsGoalsV2Data: SavingsGoalsV2Data
)

@kotlinx.serialization.Serializable
data class SavingsGoalsV2Data(
    @SerialName("question")
    val question: String,
    @SerialName("savingsGoalList")
    val savingsGoalList: List<GoalsV2>
)

@Parcelize
@kotlinx.serialization.Serializable
data class GoalsV2(
    @SerialName("title")
    val title: String,
    @SerialName("icon")
    val icon: String? = null,

    var isSelected: Boolean? = false,

    ) : Parcelable
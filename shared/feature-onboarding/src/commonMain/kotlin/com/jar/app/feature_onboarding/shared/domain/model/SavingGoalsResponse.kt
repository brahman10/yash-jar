package com.jar.app.feature_onboarding.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SavingGoalsResponse(
    @SerialName("contentType")
    val contentType: String,
    @SerialName("savingsGoalResponse")
    val savingsGoalsData: SavingsGoalsData
)

@kotlinx.serialization.Serializable
data class SavingsGoalsData(
    @SerialName("question")
    val question: String,
    @SerialName("answer")
    val answer: String,
    @SerialName("savingsGoalList")
    val savingsGoalList: List<ReasonForSavings>
)

@Parcelize
@kotlinx.serialization.Serializable
data class ReasonForSavings(
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("bgColor")
    val bgColors: List<String>? = null,
    @SerialName("isSelected")
    var isSelected: Boolean = false,
    @SerialName("other")
    val isOther: Boolean = false

) : Parcelable
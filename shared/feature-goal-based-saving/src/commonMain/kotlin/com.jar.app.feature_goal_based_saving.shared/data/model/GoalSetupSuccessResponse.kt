package com.jar.app.feature_goal_based_saving.shared.data.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoalSetupSuccessResponse (
    @SerialName("isSuccess")
    val isSuccess: Boolean? = true,
    @SerialName("heading")
    val heading: String? = "You have reached your goal!",
    @SerialName("icon")
    val icon: Boolean? = true,
    @SerialName("savingFor")
    val savingFor: String? = "72” LCD TV",
    @SerialName("youSaved")
    val youSaved: String? = "You saved",
    @SerialName("amount")
    val amount: Int? = 12000,
    @SerialName("duration")
    val duration: String? = "in 12 months 30 days",
    @SerialName("primaryCta")
    val primaryCta: String? = "Start new goal",
    @SerialName("secondaryCta")
    val secondaryCta: String? = "Withdraw",
    @SerialName("primaryCtaDeepLink")
    val primaryCtaDeepLink: String? = "Start new goal",
    @SerialName("secondaryCtaDeepLink")
    val secondaryCtaDeepLink: String? = "Withdraw",
)
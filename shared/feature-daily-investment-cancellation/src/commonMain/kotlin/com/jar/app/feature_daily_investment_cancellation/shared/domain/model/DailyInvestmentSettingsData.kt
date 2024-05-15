package com.jar.app.feature_daily_investment_cancellation.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DailyInvestmentSettingsData(
    @SerialName("savingsDetails")
    val savingsDetails: SavingsDetails? = null,

    @SerialName("stepsFeaturesDetails")
    val stepsFeaturesDetails: StepsFeaturesDetails? = null,

    @SerialName("setupDetails")
    val setupDetails: SetupDetails? = null,

    @SerialName("confirmDsActionDetails")
    val confirmDsActionDetails: ConfirmDsActionDetails? = null,

    @SerialName("userSettingsButtons")
    val userSettingsButtons: UserSettingsButtons? = null,

    @SerialName("subVersion")
    val subVersion: String? = null,
)

@kotlinx.serialization.Serializable
data class SavingsDetails(
    @SerialName("stateView")
    val stateView: StateView? = null,

    @SerialName("savedText")
    val savedText: String? = null,

    @SerialName("totalDsAmount")
    val totalDsAmount: String? = null,

    @SerialName("savingInText")
    val savingInText: String? = null,

    @SerialName("calendarCtaText")
    val calendarCtaText: String? = null,

    @SerialName("autoDebitDateText")
    val autoDebitDateText: String? = null

    )

@kotlinx.serialization.Serializable
data class StepsFeaturesDetails(
    @SerialName("header")
    val header: String? = null,

    @SerialName("stepsOrderText")
    val stepsOrderText: Map<Int,String>? = null,

    @SerialName("featuresOrderText")
    val featuresOrderText: Map<Int,String>? = null,

    @SerialName("featureOrderTextDeeplinks")
    val featureOrderTextDeeplinks: Map<Int, String?>? = null,

    @SerialName("recurringAmount")
    val recurringAmount: Float? = null,

    )

@kotlinx.serialization.Serializable
data class SetupDetails(
    @SerialName("header")
    val header: String? = null,

    @SerialName("detailsOrderText")
    val detailsOrderText: Map<Int,String>? = null,

    @SerialName("status")
    val status: String? = null,

    @SerialName("amount")
    val amount: Float? = null,

    @SerialName("subProvider")
    val subProvider: String? = null,

    @SerialName("dsDisableDate")
    val dsDisableDate: String? = null,

    )

@kotlinx.serialization.Serializable
data class ConfirmDsActionDetails(
    @SerialName("deeplink")
    val deeplink: String? = null,
)

@kotlinx.serialization.Serializable
data class UserSettingsButtons(
    @SerialName("leftButtonText")
    val leftButtonText: String? = null,

    @SerialName("rightButtonText")
    val rightButtonText: String? = null,
)

@kotlinx.serialization.Serializable
data class StateView(
    @SerialName("text")
    val text: String? = null,

    @SerialName("buttonText")
    val buttonText: String? = null,
)


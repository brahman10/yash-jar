package com.jar.app.feature_goal_based_saving.shared.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
data class MergeGoalResponse(
    @SerialName("title")
    val title: String? = null,
    @SerialName("header1")
    val header1: String? = null,
    @SerialName("header2")
    val header2: String? = null,
    @SerialName("subscriptionSetupDetails")
    val subscriptionSetupDetails: List<SubscriptionSetupDetail>? = null,
    @SerialName("totalAmountInfo")
    val totalAmountInfo: TotalAmountInfo? = null,
    @SerialName("privacyText")
    val privacyText: String? = null,
    @SerialName("privacyIcon")
    val privacyIcon: String? = null,
    @SerialName("socialProofingText")
    val socialProofingText: String? = null,
    @SerialName("npciIcon")
    val npciIcon: String? = null,
    @SerialName("secureIcon")
    val secureIcon: String? = null,
    @SerialName("footerButtonText")
    val footerButtonText: String? = null
)

@Serializable
data class SubscriptionSetupDetail(
    @SerialName("subscriptionName")
    val subscriptionName: String? = null,
    @SerialName("setupStateInfo")
    val setupStateInfo: String? = null,
    @SerialName("amountInfo")
    val amountInfo: String? = null,
    @SerialName("settingUp")
    val settingUp: Boolean? = null
)

@Serializable
data class TotalAmountInfo(
    @SerialName("title")
    val title: String? = null,
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("amountText")
    val amountText: String? = null,
    @SerialName("amountCalculationHeader")
    val amountCalculationHeader: String? = null,
    @SerialName("amountCalculationAnswer")
    val amountCalculationAnswer: String? = null
)

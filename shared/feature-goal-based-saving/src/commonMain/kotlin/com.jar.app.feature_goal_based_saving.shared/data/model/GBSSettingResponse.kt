package com.jar.app.feature_goal_based_saving.shared.data.model

import kotlinx.serialization.SerialName

import kotlinx.serialization.Serializable

@Serializable
data class GBSSettingResponse(
    @SerialName("goalId")
    val goalId: String? = null,
    @SerialName("progressStatus")
    val progressStatus: String? = null,
    @SerialName("progressResponse")
    val progressResponse: ProgressResponse? = null,
    @SerialName("goalCompletedResponse")
    val goalCompletedResponse: GoalCompletedResponse? = null
)

@Serializable
data class ProgressResponse(
    @SerialName("title")
    val title: String? = null,
    @SerialName("banner")
    val banner: Button? = null,
    @SerialName("setupDetails")
    val setupDetails: SetupDetails? = null,
    @SerialName("autoSaveDetails")
    val autoSaveDetails: AutoSaveDetails? = null,
    @SerialName("trackGoalButton")
    val trackGoalButton: Button? = null,
    @SerialName("endGoalButton")
    val endGoalButton: Button? = null
)

@Serializable
data class SetupDetails(
    @SerialName("header")
    val header: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("goalDetails")
    val goalDetails: GoalDetails? = null
)

@Serializable
data class GoalDetails(
    @SerialName("header")
    val header: String? = null,
    @SerialName("showChevron")
    val showChevron: Boolean? = null,
    @SerialName("goalDetailList")
    val goalDetailList: List<GoalDetailItem>? = null
)

@Serializable
data class AutoSaveDetails(
    @SerialName("header")
    val header: String? = null,
    @SerialName("showChevron")
    val showChevron: Boolean? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("details")
    val details: List<DetailsItem>? = null,
    var isExpanded: Boolean = false
)

@Serializable
data class GoalCompletedResponse(
    @SerialName("showEndState")
    val showEndState: Boolean? = null,
    @SerialName("activeResponse")
    val activeResponse: ActiveResponse? = null,
    @SerialName("endStateResponse")
    val endStateResponse: EndStateResponse? = null
)

@Serializable
data class ActiveResponse(
    @SerialName("title")
    val title: String? = null,
    @SerialName("investedAmount")
    val investedAmount: String? = null,
    @SerialName("totalAmountDesc")
    val totalAmountDesc: String? = null,
    @SerialName("prevPercentage")
    val prevPercentage: Double? = null,
    @SerialName("currPercentage")
    val currPercentage: Double? = null,
    @SerialName("celebrationLottie")
    val celebrationLottie: String? = null,
    @SerialName("trackMessage")
    val trackMessage: TrackMessage? = null,
    @SerialName("popup")
    val popup: Popup? = null,
    @SerialName("goalDetails")
    val goalDetails: StatusDetails? = null,
    @SerialName("settings")
    val settings: Button? = null,
    @SerialName("isDailySavingsDisabled")
    val isDailySavingsDisabled: Boolean? = null,
    @SerialName("dailyAmount")
    val dailyAmount: Long? = null,

)

@Serializable
data class TrackMessage(
    @SerialName("iconLink")
    val iconLink: String? = null,
    @SerialName("text")
    val text: String? = null,
    @SerialName("buttonText")
    val buttonText: String? = null,
    @SerialName("deeplink")
    val deeplink: String? = null
)

@Serializable
data class Popup(
    @SerialName("iconLink")
    val iconLink: String? = null,
    @SerialName("text")
    val text: String? = null,
    @SerialName("buttonText")
    val buttonText: String? = null,
    @SerialName("deeplink")
    val deeplink: String? = null
)

@Serializable
data class EndStateResponse(
    @SerialName("title")
    val title: String? = null,
    @SerialName("header")
    val header: String? = null,
    @SerialName("goalImage")
    val goalImage: String? = null,
    @SerialName("goalName")
    val goalName: String? = null,
    @SerialName("lottie")
    val lottie: String? = null,
    @SerialName("investmentHeader")
    val investmentHeader: String? = null,
    @SerialName("investedAmount")
    val investedAmount: String? = null,
    @SerialName("timeDesc")
    val timeDesc: String? = null,
    @SerialName("newGoalButton")
    val newGoalButton: Button? = null,
    @SerialName("withdrawButton")
    val withdrawButton: Button? = null
)


@Serializable
data class GoalDetailItem(
    @SerialName("key")
    val key: String? = null,
    @SerialName("value")
    val value: String? = null,
    @SerialName("copy")
    val copy: Boolean? = null
)

@Serializable
data class DetailsItem(
    @SerialName("key")
    val key: String? = null,
    @SerialName("value")
    val value: String? = null,
    @SerialName("copy")
    val copy: Boolean? = null
)

@Serializable
data class StatusDetails(
    @SerialName("goalImage")
    val goalImage: String? = null,
    @SerialName("goalName")
    val goalName: String? = null,
    @SerialName("amount")
    val amount: String? = null,
    @SerialName("details")
    val details: List<DetailsItem>? = null,
    @SerialName("invoiceLink")
    val invoiceLink: String? = null,
    @SerialName("invoiceText")
    val invoiceText: String? = null
)



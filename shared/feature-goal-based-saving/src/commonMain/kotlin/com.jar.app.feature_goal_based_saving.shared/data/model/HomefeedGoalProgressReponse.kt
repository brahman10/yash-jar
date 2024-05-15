package com.jar.app.feature_goal_based_saving.shared.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomefeedGoalProgressReponse(
    @SerialName("goalId")
    val goalId: String? = null,
    @SerialName("progressStatus")
    var progressStatus: String? = null,
    @SerialName("inProgressResponse")
    val inProgressResponse: InProgressResponse? = null,
    @SerialName("activeResponse")
    val activeResponse: ActiveResponse? = null,
    @SerialName("goalCompletedResponse")
    val goalCompletedResponse: GoalCompletedResponse? = null,
    @SerialName("endStateResponse")
    val endStateResponse: EndStateResponse? = null
)
//
@Serializable
data class InProgressResponse(
    @SerialName("title")
    val title: String? = null,
    @SerialName("banner")
    val banner: Button? = null,
    @SerialName("header")
    val header: String? = null,
    @SerialName("goalMessage")
    val goalMessage: String? = null,
    @SerialName("bottomHeader")
    val bottomHeader: String? = null,
    @SerialName("dailyAmount")
    val dailyAmount: String? = null,
    @SerialName("dailyAmountDesc")
    val dailyAmountDesc: String? = null,
    @SerialName("goalImage")
    val goalImage: String? = null
)

enum class ProgressStatus {
    ACTIVE,
    IN_PROGRESS,
    SETUP,
    COMPLETED;

    companion object {
        fun fromString(value: String?): ProgressStatus {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: ACTIVE
        }
    }
}

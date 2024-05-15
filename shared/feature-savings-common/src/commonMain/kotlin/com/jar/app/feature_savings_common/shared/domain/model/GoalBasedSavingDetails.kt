package com.jar.app.feature_savings_common.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class GoalBasedSavingDetails(
    @SerialName("dailySavingsType")
    val dailySavingsType: String? = null,

    @SerialName("goalProgressStatus")
    val goalProgressStatus: String? = null,

    @SerialName("savingsGoalCta")
    val messageCta: MessageCta? = null,

    ) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class MessageCta(
    @SerialName("iconLink")
    val iconLink: String? = null,

    @SerialName("text")
    val text: String? = null,

    @SerialName("buttonText")
    val buttonText: String? = null,

    @SerialName("deeplink")
    val deeplink: String? = null
) : Parcelable

enum class UserSavingType {
    DAILY_SAVINGS,
    SAVINGS_GOAL;
    companion object {
        fun fromString(value: String?): UserSavingType {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: UserSavingType.DAILY_SAVINGS
        }
    }
}

enum class GoalProgressStatus {
    ACTIVE,
    SETUP;
    companion object {
        fun fromString(value: String?): GoalProgressStatus {
            return GoalProgressStatus.values()
                .find { it.name.equals(value, ignoreCase = true) } ?: GoalProgressStatus.SETUP
        }
    }
}
package com.jar.app.feature_sell_gold.shared.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WithdrawHelpData(
    @SerialName("param")
    val param: Int,

    @SerialName("contentType")
    val contentType: String,

    @SerialName("quickActionWithdraw")
    val quickActionWithdraw: QuickActionWithdraw
)

@Serializable
data class QuickActionWithdraw(
    @SerialName("title")
    val title: String? = null,

    @SerialName("stepsList")
    val stepsList: ArrayList<Steps>? = null,

    @SerialName("imageUrl")
    val imageUrl: String? = null,

    @SerialName("footerButtonText")
    val footerButtonText: String?,

    @SerialName("footerText")
    val footerText: String? = null,

    @SerialName("profilePics")
    val profilePics: ArrayList<String>? = null,

    @SerialName("withdrawalLimitBottomSheet")
    val withdrawalLimitBottomSheet: WithdrawalLimitBottomSheet? = null,

    @SerialName("deepLink")
    val deepLink: String? = null,
)

@Serializable
data class Steps(
    @SerialName("title")
    val title: String,

    @SerialName("iconUrl")
    val iconUrl: String
)

@Serializable
data class WithdrawalLimitBottomSheet(
    @SerialName("title")
    val title: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("iconLink")
    val iconLink: String?,
    @SerialName("backgroundColor")
    val backgroundColor: String?,
    @SerialName("buttonText")
    val buttonText: String?,
    @SerialName("timerText")
    val timerText: String?
)

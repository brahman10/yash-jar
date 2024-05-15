package com.jar.app.feature_refer_earn_v2.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReferIntroScreenData(
    @SerialName("contactsSynced")
    val contactsSynced: Boolean? = null,
    @SerialName("newRewardsText")
    val newRewardsText: String? = null,
    @SerialName("referralCount")
    val referralCount: Int? = null,
    @SerialName("hasWinnings")
    val hasWinnings: Boolean? = false,
    @SerialName("rewards")
    val rewards: List<RewardData>? = null,
    @SerialName("shareImage")
    val shareImage: String? = null,
    @SerialName("staticContent")
    val staticContent: StaticContent? = null,
)
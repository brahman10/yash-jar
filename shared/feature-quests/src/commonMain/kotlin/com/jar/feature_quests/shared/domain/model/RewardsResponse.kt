package com.jar.feature_quests.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RewardsResponse(
    @SerialName("title")
    val title: String,
    @SerialName("rewardsList")
    val rewardsList: List<RewardItem>,
    @SerialName("allRewardsViewItems")
    val allRewardsViewItems: AllRewardsViewItems,
    @SerialName("lockedRewardsCount")
    val lockedRewardsCount: Int? = null,
    @SerialName("unlockedRewardsCount")
    val unlockedRewardsCount: Int? = null,
    @SerialName("missedRewardsCount")
    val missedRewardsCount: Int? = null
)
@Serializable
data class AllRewardsViewItems(
    @SerialName("bgCoin1")
    val bgCoin1: String,
    @SerialName("bgCoin2")
    val bgCoin2: String,
    @SerialName("bgCoin3")
    val bgCoin3: String,
    @SerialName("bgCoin4")
    val bgCoin4: String,
    @SerialName("bgCoin5")
    val bgCoin5: String,
    @SerialName("bgCoin6")
    val bgCoin6: String,
)
@Serializable
data class RewardItem(
    @SerialName("icon")
    val icon: String,
    @SerialName("iconColor")
    val iconColor: String,
    @SerialName("backgroundColor")
    val backgroundColor: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("bottomText")
    val bottomText: String?,
    @SerialName("bottomColor")
    val bottomColor: String,
    @SerialName("shimmerUrl")
    val shimmerUrl: String? = null,
    @SerialName("deeplink")
    val deeplink: String? = null
)

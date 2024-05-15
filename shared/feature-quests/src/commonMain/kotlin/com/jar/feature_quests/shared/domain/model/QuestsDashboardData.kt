package com.jar.feature_quests.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class QuestsDashboardData(
    @SerialName("bgCoinImage1")
    val bgCoinImage1: String? = null,
    @SerialName("bgCoinImage2")
    val bgCoinImage2: String? = null,
    @SerialName("bgCoinImage3")
    val bgCoinImage3: String? = null,
    @SerialName("bgCoinImage4")
    val bgCoinImage4: String? = null,
    @SerialName("questBanner")
    val questBanner: String? = null,
    @SerialName("quests")
    val quests: List<Quest>,
    @SerialName("toolbarText")
    val toolbarText: String? = null,
    @SerialName("welcomeRewardQuest")
    val welcomeRewardQuest: Quest
) {
    fun getCurrentQuestPairForEvent(): Pair<Int, Quest?> {
        quests.forEachIndexed { index, quest ->
            if (quest.getStatusEnum() == QuestStatus.UNLOCKED || quest.getStatusEnum() == QuestStatus.IN_PROGRESS) {
                return Pair(index, quest)
            }
        }
        return Pair(-1, null)
    }
}
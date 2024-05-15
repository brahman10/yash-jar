package com.jar.feature_quests.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class QuestCardCta(
    @SerialName("buttonType")
    val buttonType: String? = null,
    @SerialName("clickAction")
    private val clickAction: String,
    @SerialName("deepLink")
    val deeplink: String? = null,
    @SerialName("title")
    val title: String
) {
    fun getClickActionEnum(): QUEST_CLICK_ACTION {
        return QUEST_CLICK_ACTION.valueOf(clickAction)
    }
}

enum class QUEST_CLICK_ACTION {
    QUEST_HOME,
    SPIN_GAME,
    QUIZ_GAME,
    TXN_GAME,
    VIEW_REWARDS,
    REWARD_POP_UP,
    QUIZ_BOTTOM_SHEET,
    DEEP_LINK
}
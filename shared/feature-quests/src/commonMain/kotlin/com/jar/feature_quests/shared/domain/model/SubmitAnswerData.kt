package com.jar.feature_quests.shared.domain.model

import com.jar.app.core_base.domain.model.JackPotResponseV2
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SubmitAnswerData(
    @SerialName("ansMarkedCorrectly")
    val ansMarkedCorrectly: Boolean? = null,
    @SerialName("actualCorrectAns")
    val actualCorrectAns: String? = null,
    @SerialName("action")
    private val action: String? = null,
    @SerialName("chancesLeftBottomSheet")
    val chancesLeftBottomSheet: ChancesLeftData? = null,
    @SerialName("couponResponse")
    val couponResponse: JackPotResponseV2? = null
) {
    fun getAction() = SubmitAnswerAction.values().find { it.name == action } ?: SubmitAnswerAction.NONE
}

enum class SubmitAnswerAction {
    REWARD_POP_UP,
    QUIZ_BOTTOM_SHEET,
    NONE //Do Nothing
}
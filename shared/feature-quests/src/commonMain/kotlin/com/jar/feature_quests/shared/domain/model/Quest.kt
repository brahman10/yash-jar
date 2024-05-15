package com.jar.feature_quests.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class Quest(
    @SerialName("cardDetails")
    val cardDetails: CardDetails? = null,
    @SerialName("indicator")
    val indicator: Indicator? = null,
    @SerialName("locked")
    val locked: Boolean? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("status")
    private val status: String? = null
) {
    fun getStatusEnum(): QuestStatus {
        return QuestStatus.valueOf(status!!)
    }
}

enum class QuestStatus {
    LOCKED,
    UNLOCKED,
    IN_PROGRESS,
    COMPLETED
}
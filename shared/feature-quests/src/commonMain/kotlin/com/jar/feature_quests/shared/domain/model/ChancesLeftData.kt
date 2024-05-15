package com.jar.feature_quests.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ChancesLeftData(
    @SerialName("title")
    val title: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("chancesLeft")
    val chancesLeft: List<String>? = null,
    @SerialName("tryAgainCta")
    val tryAgainCta: TryAgainCta? = null,
    @SerialName("questHomeCta")
    val questHomeCta: QuestHomeCta? = null,
    @SerialName("chancesLeftCount")
    val chancesLeftCount: Int? = null
)
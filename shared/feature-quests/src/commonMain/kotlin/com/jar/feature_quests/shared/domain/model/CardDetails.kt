package com.jar.feature_quests.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class CardDetails(
    @SerialName("bgColorEnd")
    val bgColorEnd: String? = null,
    @SerialName("bgColorStart")
    val bgColorStart: String,
    @SerialName("bgOverlayImage")
    val bgOverlayImage: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("image")
    val image: String? = null,
    @SerialName("questCardCta")
    val questCardCta: QuestCardCta? = null,
    @SerialName("progress")
    val progress: List<String?>? = null,
    @SerialName("title")
    val title: String? = null
)
package com.jar.feature_quests.shared.domain.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class Indicator(
    @SerialName("bgColorEnd")
    val bgColorEnd: String? = null,
    @SerialName("bgColorStart")
    val bgColorStart: String? = null,
    @SerialName("iconUrl")
    val iconUrl: String? = null,
    @SerialName("text")
    val text: String? = null
)
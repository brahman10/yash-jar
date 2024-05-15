package com.jar.app.feature_weekly_magic_common.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class WeeklyChallengeInfo(
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("cardTitle")
    val cardTitle: String,
    @SerialName("cardDescription")
    val cardDescription: String,
    @SerialName("ctaText")
    val ctaText: String? = null,
    @SerialName("ctaDeeplink")
    val ctaDeeplink: String? = null,
    @SerialName("challengeId")
    val challengeId: String? = null
)
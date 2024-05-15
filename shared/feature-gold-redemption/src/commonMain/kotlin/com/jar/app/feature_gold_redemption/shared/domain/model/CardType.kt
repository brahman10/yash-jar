package com.jar.app.feature_gold_redemption.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class CardType {
    @SerialName("1")
    NONE,

    @SerialName("GOLD")
    GOLD,

    @SerialName("DIAMOND")
    DIAMOND,
}
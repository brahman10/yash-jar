package com.jar.app.feature_gold_redemption.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class CardStatus {
    @SerialName("PROCESSING")
    PROCESSING,

    @SerialName("ACTIVE")
    ACTIVE,

    @SerialName("EXPIRED")
    EXPIRED,

    @SerialName("FAILED")
    FAILED,
}
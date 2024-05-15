package com.jar.app.feature_gold_sip.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldSipIntroData(
    @SerialName("contentType")
    val contentType: String,
    @SerialName("updateGoldSipEducationalInfo")
    val goldSipIntro: GoldSipIntro
)

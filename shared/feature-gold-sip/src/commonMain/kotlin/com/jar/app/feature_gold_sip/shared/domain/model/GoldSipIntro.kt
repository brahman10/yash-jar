package com.jar.app.feature_gold_sip.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldSipIntro(
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val goldSipDataList: List<GoldSipData>
)
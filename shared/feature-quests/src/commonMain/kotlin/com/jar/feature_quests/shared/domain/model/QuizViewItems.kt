package com.jar.feature_quests.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class QuizViewItems(
    @SerialName("bgCoin1")
    val bgCoin1: String? = null,
    @SerialName("bgCoin2")
    val bgCoin2: String? = null,
    @SerialName("bgCoin3")
    val bgCoin3: String? = null
)
package com.jar.app.feature_gold_delivery.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GenericFAQ(
    @SerialName("answer")
    val answer: String? = null,
    @SerialName("question")
    val question: String? = null
)
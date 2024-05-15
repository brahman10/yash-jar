package com.jar.app.feature_gold_redemption.shared.data.network.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenericFAQ(
    @SerialName("answer")
    val answer: String? = null,
    @SerialName("question")
    val question: String? = null
)
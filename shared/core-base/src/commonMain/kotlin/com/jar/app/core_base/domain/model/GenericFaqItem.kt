package com.jar.app.core_base.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenericFaqItem(
    @SerialName("question")
    val question: String,
    @SerialName("answer")
    val answer: String,
    @SerialName("isExpanded")
    val isExpanded: Boolean = false
)
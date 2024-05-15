package com.jar.app.feature_daily_investment.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ExpandableFaqResponse(
    @SerialName("contentType")
    val contentType: String,

    @SerialName("genericFAQResponse")
    val genericFAQResponse: GenericFAQResponse
)

@kotlinx.serialization.Serializable
data class GenericFAQResponse(
    @SerialName("genericFAQs")
    val genericFAQs: List<GenericFAQs>
)

@kotlinx.serialization.Serializable
data class GenericFAQs(
    @SerialName("question")
    val question: String,

    @SerialName("answer")
    val answer: String,

    var isExpanded: Boolean = false
)
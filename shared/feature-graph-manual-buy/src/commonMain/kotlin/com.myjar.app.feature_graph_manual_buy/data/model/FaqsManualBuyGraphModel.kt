package com.myjar.app.feature_graph_manual_buy.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FaqsResponse(
    @SerialName("faqsList")
    val faqsList: List<FAQsItem>? = null
)

@Serializable
data class FAQsItem(
    @SerialName("type")
    val type: String? = null,

    @SerialName("faqs")
    val faqs: List<FAQ>? = null
)

@Serializable
data class FAQ(
    @SerialName("question")
    val question: String,

    @SerialName("answer")
    val answer: String
)

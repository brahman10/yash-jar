package com.jar.app.feature_calculator.shared.domain.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CalculatorDataRes(
    @SerialName("cardDetails")
    val cardDetails: CalculatorCardData,
    @SerialName("disclaimerText")
    val disclaimerText: String,
    @SerialName("sliderData")
    val sliderData: List<SliderData>,
    @SerialName("title")
    val title: String,
    @SerialName("type")
    val type: String,
    @SerialName("faqResponse")
    val faqResponse: FaqResponse? = null

)
@Serializable
data class FaqResponse(
    @SerialName("type")
    val title: String,
    @SerialName("faqs")
    val faqs: List<FAQ>

)
@Serializable
data class FAQ(
    @SerialName("question")
    val question: String,
    @SerialName("answer")
    val answer: String
)
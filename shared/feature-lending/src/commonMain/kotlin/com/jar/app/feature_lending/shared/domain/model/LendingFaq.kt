package com.jar.app.feature_lending.shared.domain.model

import com.jar.app.core_base.domain.model.Faq
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class LendingFaq(
    @SerialName("lendingFAQs")
    val lendingFAQs: LendingEligibilityFaqDetails? = null
)

@kotlinx.serialization.Serializable
data class LendingEligibilityFaqDetails(
    @SerialName("faqsList")
    val faqDataList: List<FaqData>
)

@kotlinx.serialization.Serializable
data class FaqData(
    @SerialName("faqs")
    val faqs: List<Faq>,

    @SerialName("type")
    val type: String
)
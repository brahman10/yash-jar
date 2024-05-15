package com.jar.app.feature_onboarding.shared.domain.model

import com.jar.app.core_base.domain.model.FaqList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FaqStaticData(
    @SerialName("generalFaqs")
    val faqList: FaqList? = null,
    @SerialName("smsFAQs")
    val smsFAQs: FaqList? = null,
    @SerialName("goldFAQs")
    val goldFaq: FaqList? = null,
)
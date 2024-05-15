package com.jar.app.core_base.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class FaqData(
    @SerialName("faqs")
    val faqs: List<Faq>,

    @SerialName("type")
    val type: String
)
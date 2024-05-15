package com.jar.app.core_base.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class FaqList(
    @SerialName("faqsList")
    val faqDataList: List<FaqData>
)
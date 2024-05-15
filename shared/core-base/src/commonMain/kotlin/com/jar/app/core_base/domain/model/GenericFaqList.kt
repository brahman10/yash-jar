package com.jar.app.core_base.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenericFaqList(
    @SerialName("genericFAQs")
    val genericFAQs:List<GenericFaqItem>
)

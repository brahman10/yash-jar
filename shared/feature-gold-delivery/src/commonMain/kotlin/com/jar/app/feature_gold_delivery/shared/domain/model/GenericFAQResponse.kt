package com.jar.app.feature_gold_delivery.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GenericFAQResponse(
    @SerialName("genericFAQs")
    val genericFAQS: List<GenericFAQ>
)
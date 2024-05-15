package com.jar.app.feature_gold_delivery.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldDeliveryFaq(
    @SerialName("contentType")
    val contentType: String? = null,
    @SerialName("genericFAQResponse")
    val genericFAQResponse: GenericFAQResponse? = null,
)

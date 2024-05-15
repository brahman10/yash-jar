package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ForeclosureData(
    @SerialName("details")
    val details: List<KeyValueData>? = null,
    @SerialName("foreclosureEnabled")
    val foreclosureEnabled: Boolean? = null,
    @SerialName("message")
    val message: String? = null,
    @SerialName("totalAmount")
    val totalAmount: Float? = null
)
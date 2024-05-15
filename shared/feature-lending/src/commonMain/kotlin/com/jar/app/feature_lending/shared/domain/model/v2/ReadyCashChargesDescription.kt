package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ReadyCashChargesDescription(
    @SerialName("description")
    val description: String? = null,
    @SerialName("heading")
    val heading: String? = null,
    @SerialName("subHeading")
    val subHeading: String? = null
)
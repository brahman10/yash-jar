package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MandateDetailsV2(
    @SerialName("mandateAuthType")
    val mandateAuthType: String? = null,
    @SerialName("mandateLink")
    val mandateLink: String? = null,
    @SerialName("provider")
    val provider: String? = null,
    @SerialName("status")
    val status: String? = null
)
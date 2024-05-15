package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AadhaarContent(
    @SerialName("consent")
    val consent: String? = null,
    @SerialName("manualEnsure")
    val manualEnsure: String? = null,
    @SerialName("sampleUrl")
    val sampleUrl: String? = null
)
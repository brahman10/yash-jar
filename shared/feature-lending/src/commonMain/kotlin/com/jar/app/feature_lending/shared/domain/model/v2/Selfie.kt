package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Selfie(
    @SerialName("selfie")
    val selfie: String? = null,
    @SerialName("status")
    val status: String? = null
)
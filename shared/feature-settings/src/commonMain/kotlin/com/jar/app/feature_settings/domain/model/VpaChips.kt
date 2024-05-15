package com.jar.app.feature_settings.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class VpaChips(
    @SerialName("vpaChips")
    val vpaChips: List<String>
)
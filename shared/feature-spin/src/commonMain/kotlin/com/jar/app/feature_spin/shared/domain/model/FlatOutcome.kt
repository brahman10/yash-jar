package com.jar.app.feature_spin.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class FlatOutcome(
    @SerialName("outcome")
    val outcome: Int,

    @SerialName("outcomeType")
    val outcomeType: String? = null,

    @SerialName("preText")
    val preText: String? = null,

    @SerialName("postText")
    val postText: String? = null,

    @SerialName("winningsIconLink")
    val winningsIconLink: String? = null
)

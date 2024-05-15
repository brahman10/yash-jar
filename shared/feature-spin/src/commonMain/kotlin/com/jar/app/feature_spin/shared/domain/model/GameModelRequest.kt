package com.jar.app.feature_spin.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameModelRequest(
    @SerialName("gameId")
    val gameId: String? = null
)

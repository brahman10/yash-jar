package com.jar.app.feature_transaction.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserWinningBreakdownModel(
    @SerialName("keys")
    val keys: List<String>? = null,
    @SerialName("values")
    val values: List<Float>? = null,
    @SerialName("totalWinningsReceived")
    val totalWinningsReceived: Float? = null
)
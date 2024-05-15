package com.jar.app.feature_settings.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DeleteCard(
    @SerialName("cardToken")
    val cardToken: String
)
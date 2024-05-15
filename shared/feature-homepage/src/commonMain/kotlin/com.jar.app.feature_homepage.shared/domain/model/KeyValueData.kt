package com.jar.app.feature_homepage.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KeyValueData(
    @SerialName("key")
    val key: String?,
    @SerialName("value")
    val value: String?,
)

package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class KeyValueData(
    @SerialName("key")
    val key: String? = null,
    @SerialName("value")
    val value: String? = null
)
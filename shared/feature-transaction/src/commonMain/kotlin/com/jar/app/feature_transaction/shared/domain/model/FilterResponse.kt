package com.jar.app.feature_transaction.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class FilterResponse(
    @SerialName("filter")
    val key: String,
    @SerialName("values")
    val values: List<String>,
    @SerialName("filterName")
    val keyName: String,
    @SerialName("valueNames")
    val valueNames: List<String>,
)
package com.jar.app.feature_transaction.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class IndividualFilterObject(
    @SerialName("filterEnums")
    val filterEnums: String? = null,
    @SerialName("subFilters")
    val subFilters: List<String>? = null
)
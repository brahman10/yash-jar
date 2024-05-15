package com.jar.app.feature_health_insurance.shared.data.models.benefits

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Benefit(
    @SerialName("header")
    val header: String,
    @SerialName("id")
    val id: String,
    @SerialName("subText")
    val subText: String,

    var isExpanded: Boolean = false
)
package com.jar.app.feature_sell_gold.shared.domain.models

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class WorkFlowData(
    @SerialName("description")
    val description: String? = null,
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("title")
    val title: String? = null
)
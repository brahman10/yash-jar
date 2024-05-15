package com.jar.app.feature_sell_gold.shared.domain.models

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class WorkFlowPage(
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("workFlowData")
    val workFlowData: List<WorkFlowData>? = null
)
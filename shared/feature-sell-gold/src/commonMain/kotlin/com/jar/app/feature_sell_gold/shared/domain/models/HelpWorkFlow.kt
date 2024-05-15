package com.jar.app.feature_sell_gold.shared.domain.models

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class HelpWorkFlow(
    @SerialName("header")
    val header: String? = null,
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("workFlowPages")
    val workFlowPages: List<WorkFlowPage>? = null
)
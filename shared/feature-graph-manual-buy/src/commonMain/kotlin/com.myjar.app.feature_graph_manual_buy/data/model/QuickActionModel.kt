package com.myjar.app.feature_graph_manual_buy.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuickActionResponse(
    @SerialName("quickActionList")
    val quickActionList: List<QuickActionItem>? = null
)

@Serializable
data class QuickActionItem(
    @SerialName("icon")
    val icon: String? = null,

    @SerialName("title")
    val title: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("deepLink")
    val deepLink: String,

    @SerialName("cardType")
    val cardType: String? = null,

    @SerialName("quickActionType")
    val quickActionType: String? = null,

    @SerialName("isPrimary")
    val isPrimary: Boolean? = null
)

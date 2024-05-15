package com.jar.app.feature_homepage.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class QuickActionResponse(
    @SerialName("quickActionList")
    val quickActionList: List<QuickActionData>,
    @SerialName("headerText")
    val headerText: String?
)
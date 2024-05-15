package com.jar.feature_quests.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class TryAgainCta(
    @SerialName("buttonType")
    val buttonType: String? = null,
    @SerialName("clickAction")
    val clickAction: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("deepLink")
    val deepLink: String? = null
)
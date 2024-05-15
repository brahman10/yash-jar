package com.jar.app.feature_refer_earn_v2.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReferralBreakup(
    @SerialName("description")
    val description: String? = null,
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("title")
    val title: String? = null
)
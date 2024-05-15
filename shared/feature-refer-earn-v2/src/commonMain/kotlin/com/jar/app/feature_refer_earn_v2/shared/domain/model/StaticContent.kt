package com.jar.app.feature_refer_earn_v2.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StaticContent(
    @SerialName("header")
    val header: String? = null,
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("referralBreakup")
    val referralBreakup: List<ReferralBreakup>? = null
)
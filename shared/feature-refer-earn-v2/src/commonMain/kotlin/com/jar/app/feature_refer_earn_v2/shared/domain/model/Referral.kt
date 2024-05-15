package com.jar.app.feature_refer_earn_v2.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Referral(
    @SerialName("amt")
    val amt: Double? = null,
    @SerialName("referralName")
    val referralName: String? = null,
    @SerialName("referralPhone")
    val referralPhone: String? = null,
    @SerialName("referralProgressList")
    val referralProgressList: List<ReferralProgress?>? = null,
)
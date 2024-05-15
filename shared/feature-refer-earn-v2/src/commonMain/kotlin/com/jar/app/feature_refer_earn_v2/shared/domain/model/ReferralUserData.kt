package com.jar.app.feature_refer_earn_v2.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReferralUserData(
    @SerialName("contactsSynced")
    val count: Int? = null,
    @SerialName("isReferralBlocked")
    val isReferralBlocked: Boolean? = null,
    @SerialName("referrals")
    val referrals: List<Referral>? = null,
)
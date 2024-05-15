package com.jar.app.feature_refer_earn_v2.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostReferralAttributionData(
    @SerialName("deviceDetails")
    val deviceDetails: ReferralAttributionDeviceDetails? = null,
    @SerialName("referrerUserId")
    val referrerUserId: String? = null,
)
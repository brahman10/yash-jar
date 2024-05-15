package com.jar.app.feature_refer_earn_v2.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReferralAttributionDeviceDetails(
    @SerialName("advertisingId")
    val advertisingId: String? = null,
    @SerialName("deviceId")
    val deviceId: String? = null,
    @SerialName("os")
    val osName: String? = null,
)
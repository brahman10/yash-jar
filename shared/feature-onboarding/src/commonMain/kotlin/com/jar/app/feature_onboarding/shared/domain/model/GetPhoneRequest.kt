package com.jar.app.feature_onboarding.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GetPhoneRequest(
    @SerialName("deviceId")
    val deviceId: String,

    @SerialName("advertisingId")
    val advertisingId: String,
)
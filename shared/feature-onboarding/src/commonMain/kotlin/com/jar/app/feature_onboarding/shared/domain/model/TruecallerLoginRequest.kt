package com.jar.app.feature_onboarding.shared.domain.model

import com.jar.app.feature_user_api.domain.model.DeviceDetails
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TruecallerLoginRequest(
    @SerialName("payload")
    val payload: String,

    @SerialName("signature")
    val signature: String,

    @SerialName("signatureAlgorithm")
    val signatureAlgorithm: String,

    @SerialName("deviceDetails")
    val deviceDetails: DeviceDetails,

    @SerialName("logoutFromOtherDevices")
    val logoutFromOtherDevices: Boolean? = false
)
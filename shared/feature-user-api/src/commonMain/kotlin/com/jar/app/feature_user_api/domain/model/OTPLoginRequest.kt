package com.jar.app.feature_user_api.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class OTPLoginRequest(
    @SerialName("phoneNumber")
    val phoneNumber: String? = null,

    @SerialName("countryCode")
    val countryCode: String? = null,

    @SerialName("otp")
    val otp: String,

    @SerialName("reqId")
    val reqId: String,

    @SerialName("deviceDetails")
    val deviceDetails: DeviceDetails,

    @SerialName("logoutFromOtherDevices")
    val logoutFromOtherDevices: Boolean? = false
)
package com.jar.app.feature_user_api.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class OTLLoginRequest(
    @SerialName("phoneNumber")
    val phoneNumber: String? = null,

    @SerialName("countryCode")
    val countryCode: String? = null,

    @SerialName("deviceDetails")
    val deviceDetails: DeviceDetails,

    @SerialName("logoutFromOtherDevices")
    val logoutFromOtherDevices: Boolean? = false,

    @SerialName("hasExperianConsent")
    val hasExperianConsent: Boolean? = null,

    @SerialName("correlationId")
    val correlationId: String? = null,
)
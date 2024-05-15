package com.jar.app.feature_user_api.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DeviceDetails(
    @SerialName("advertisingId")
    val advertisingId: String? = null,

    @SerialName("deviceId")
    val deviceId: String,

    @SerialName("os")
    val os: String,

    @SerialName("ipAddress")
    val ipAddress: String? = null,

    @SerialName("phoneModel")
    val phoneModel: String? = null,

    @SerialName("marketingSource")
    val marketingSource: String? = null
)
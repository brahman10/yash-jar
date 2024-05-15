package com.jar.app.feature.home.domain.model

import com.jar.app.feature_user_api.domain.model.DeviceDetails

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UserDeviceDetails(
    @SerialName("deviceDetails")
    val deviceDetails: DeviceDetails,

    @SerialName("referrerLink")
    val referrerLink: String? = null,

    @SerialName("appsFlyerId")
    val appsFlyerId: String? = null,
)
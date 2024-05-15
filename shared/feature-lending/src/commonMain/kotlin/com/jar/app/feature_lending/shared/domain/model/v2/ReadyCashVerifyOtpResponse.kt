package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ReadyCashVerifyOtpResponse(
    @SerialName("success")
    val success:Boolean?=null
)

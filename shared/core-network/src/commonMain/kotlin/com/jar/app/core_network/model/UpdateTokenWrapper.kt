package com.jar.app.core_network.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UpdateTokenWrapper(
    @SerialName("accessToken")
    val accessToken: String,

    @SerialName("refreshToken")
    val refreshToken: String,
)
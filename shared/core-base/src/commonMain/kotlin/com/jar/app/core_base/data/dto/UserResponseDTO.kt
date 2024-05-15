package com.jar.app.core_base.data.dto

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UserResponseDTO(
    @SerialName("accessToken")
    val accessToken: String,

    @SerialName("refreshToken")
    val refreshToken: String,

    @SerialName("user")
    val user: UserDTO,

    @SerialName("authType")
    val authType: String? = null,

    @SerialName("hasOtherActiveSessions")
    val hasOtherActiveSessions: Boolean? = null,

    @SerialName("numberOfDaysOfSms")
    val numberOfDaysOfSms: Int? = 0
)
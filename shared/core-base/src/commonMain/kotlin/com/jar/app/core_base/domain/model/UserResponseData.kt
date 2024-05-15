package com.jar.app.core_base.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize

@Parcelize
@kotlinx.serialization.Serializable
data class UserResponseData(
    val accessToken: String,

    val refreshToken: String,

    val user: User,

    val authType: String?,

    val hasOtherActiveSessions: Boolean?,

    val numberOfDaysOfSms: Int?
) : Parcelable
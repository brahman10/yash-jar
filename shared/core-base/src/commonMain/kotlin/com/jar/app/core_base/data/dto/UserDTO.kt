package com.jar.app.core_base.data.dto

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UserDTO(
    @SerialName("userId")
    var userId: String? = null,

    @SerialName("profilePicUrl")
    var profilePicUrl: String? = null,

    @SerialName("firstName")
    var firstName: String? = null,

    @SerialName("lastName")
    var lastName: String? = null,

    @SerialName("phoneNumber")
    val phoneNumber: String,

    @SerialName("age")
    var age: Int? = null,

    @SerialName("gender")
    var gender: String? = null,

    @SerialName("email")
    var email: String? = null,

    @SerialName("onboarded")
    var onboarded: Boolean? = null,

    @SerialName("userGoalSetup")
    var userGoalSetup: Boolean? = null,

    @SerialName("createdAt")
    val createdAtInUtc: Long
)
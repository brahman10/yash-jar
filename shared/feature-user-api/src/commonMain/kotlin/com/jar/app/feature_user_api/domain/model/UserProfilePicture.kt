package com.jar.app.feature_user_api.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UserProfilePicture(
    @SerialName("profilePicUrl")
    val profilePicUrl: String
)

@kotlinx.serialization.Serializable
data class UserKycOcr(
    @SerialName("documentType")
    val documentType: String? = null,
    @SerialName("documentId")
    val documentId: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("fathersName")
    val fathersName: String? = null,
    @SerialName("dob")
    val dob: String? = null
)
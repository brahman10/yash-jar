package com.jar.app.feature_profile.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ProfileStaticData(
    @SerialName("param")
    val param: Int = 0,

    @SerialName("avatarInfo")
    val avatarInfo: AvatarInfoResponse? = null,

    @SerialName("primaryUpiIdInfo")
    val primaryUpi: PrimaryUpi? = null
)
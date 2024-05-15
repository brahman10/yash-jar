package com.jar.app.feature_profile.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AvatarInfoResponse(
    @SerialName("femaleAvatarInfo")
    val femaleAvatarInfo: List<AvatarInfo>,

    @SerialName("maleAvatarInfo")
    val maleAvatarInfo: List<AvatarInfo>,

    @SerialName("otherAvatarInfo")
    val otherAvatarInfo: List<AvatarInfo>,
)
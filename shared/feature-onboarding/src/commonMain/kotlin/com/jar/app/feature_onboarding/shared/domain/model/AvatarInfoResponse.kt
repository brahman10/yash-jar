package com.jar.app.feature_onboarding.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AvatarInfoResponse(
    @SerialName("femaleAvatarInfo")
    val femaleAvatarInfo: List<com.jar.app.feature_onboarding.shared.domain.model.AvatarInfo>,

    @SerialName("maleAvatarInfo")
    val maleAvatarInfo: List<com.jar.app.feature_onboarding.shared.domain.model.AvatarInfo>,

    @SerialName("otherAvatarInfo")
    val otherAvatarInfo: List<com.jar.app.feature_onboarding.shared.domain.model.AvatarInfo>,
)
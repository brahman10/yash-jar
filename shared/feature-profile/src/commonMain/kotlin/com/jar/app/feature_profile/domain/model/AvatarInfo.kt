package com.jar.app.feature_profile.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AvatarInfo(
    @SerialName("image")
    val image: String,

    @SerialName("default")
    val default: Boolean? = null,

    //For UI Purpose
    @kotlinx.serialization.Transient
    val resourceId: Int? = null,

    @kotlinx.serialization.Transient
    var imageBitmap: ByteArray? = null
)
package com.jar.app.feature_jar_duo.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class DuoGroupObject(
    @SerialName("userName")
    val userName: String,

    @SerialName("duoTopObjects")
    val top: List<DuoGroupTopObject>,

    @SerialName("duoBottomObjects")
    val bottom: List<DuoGroupBottomObject>,

    @SerialName("userProfilePhoto")
    val userProfile: String? = null
) : Parcelable
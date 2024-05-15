package com.jar.app.feature_jar_duo.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class DuoGroupData(
    @SerialName("groupId")
    val groupId: String? = null,

    @SerialName("groupName")
    val groupName: String? = null,

    @SerialName("groupUserNames")
    val groupUserNames: List<String>? = null,

    @SerialName("groupProfilePictures")
    val groupProfilePictures: List<String?>? = null
) : Parcelable
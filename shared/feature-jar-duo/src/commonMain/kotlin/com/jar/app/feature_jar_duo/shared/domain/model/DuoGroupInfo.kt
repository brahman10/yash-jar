package com.jar.app.feature_jar_duo.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class DuoGroupInfo(
    @SerialName("groupName")
    val groupName: String? = null,

    @SerialName("duoGroupsUserInfo")
    val duoGroupsUserInfo: List<DuoGroupObject>,

    @SerialName("groupId")
    val groupId: String
) : Parcelable
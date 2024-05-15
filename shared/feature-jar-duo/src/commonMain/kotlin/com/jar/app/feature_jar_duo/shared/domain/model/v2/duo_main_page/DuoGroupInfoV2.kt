package com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class DuoGroupInfoV2(

    @SerialName("groupName")
    val groupName: String?,

    @SerialName("duoGroupUserRespV2s")
    val duoGroupsUserInfo: List<DuoGroupObjectV2>,

    @SerialName("duoBottomObjectDetailsV2s")
    val duoBottomObject: List<DuoGroupBottomObjectV2>,

    @SerialName("groupId")
    val groupId: String,

    @SerialName("weekDaysLeft")
    val weekDaysLeft: Int
) : Parcelable
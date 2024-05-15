package com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class DuoBottomObjectUsersDetailsV2s(
    @SerialName("userProfilePhoto")
    val image: String?,
    @SerialName("score")
    val score: Int,
    @SerialName("userName")
    val userName: String
) : Parcelable

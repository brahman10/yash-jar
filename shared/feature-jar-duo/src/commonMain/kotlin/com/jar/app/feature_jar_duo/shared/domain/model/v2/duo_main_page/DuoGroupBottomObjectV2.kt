package com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class DuoGroupBottomObjectV2(

    @SerialName("header")
    val header: String,

    @SerialName("duoBottomObjectUsersDetailsV2s")
    val scores: List<DuoBottomObjectUsersDetailsV2s>,

    @SerialName("iconLink")
    val iconLink: String,

    @SerialName("buttonDisplayText")
    val buttonDisplayText: String?,

    @SerialName("deepLink")
    val deepLink: String?
) : Parcelable
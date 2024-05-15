package com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class DuoGroupTopObjectV2(

    @SerialName("iconLink")
    val iconLink: String,

    @SerialName("enabled")
    val enabled: Boolean,

    @SerialName("displayText")
    val displayText: String,

    @SerialName("deeplink")
    val deepLink: String
) : Parcelable
package com.jar.app.feature_jar_duo.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class DuoGroupBottomObject(

    @SerialName("header")
    val header: String,

    @SerialName("value")
    val value: String
) : Parcelable
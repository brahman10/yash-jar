package com.jar.app.core_base.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class InfoItem(

    @SerialName("icon")
    val icon: String? = null,

    @SerialName("title")
    var title: String? = null,

    @SerialName("description")
    val description: String? = null
) : Parcelable
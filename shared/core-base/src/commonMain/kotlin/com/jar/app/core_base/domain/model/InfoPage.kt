package com.jar.app.core_base.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class InfoPage(

    @SerialName("title")
    val title: String,

    @SerialName("workFlowData")
    val infoItems: List<InfoItem>
) : Parcelable
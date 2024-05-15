package com.jar.app.core_base.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class InfoDialogData(
    @SerialName("header")
    val title: String?,

    @SerialName("icon")
    val icon: String?,

    @SerialName("workFlowPages")
    val infoPages: List<InfoPage>
) : Parcelable
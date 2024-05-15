package com.jar.app.feature_transaction.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class StatusInfo(
    @SerialName("bgColor")
    val bgColor: String,
    @SerialName("textColor")
    val textColor: String,
    @SerialName("statusTxt")
    val statusTxt: String,
    @SerialName("icon")
    val iconUrl: String? = null
) : Parcelable
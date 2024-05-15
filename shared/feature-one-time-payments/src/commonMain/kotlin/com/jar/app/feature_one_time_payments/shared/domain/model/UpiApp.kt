package com.jar.app.feature_one_time_payments.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class UpiApp(
    @SerialName("packageName")
    val packageName: String,

    @SerialName("appName")
    val appName: String,

    @SerialName("isSelected")
    var isSelected: Boolean? = false
) : Parcelable
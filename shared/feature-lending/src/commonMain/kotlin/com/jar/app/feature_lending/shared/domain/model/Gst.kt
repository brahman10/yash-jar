package com.jar.app.feature_lending.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class Gst(
    @SerialName("actualAmt")
    val actualAmt: Float? = null,
    @SerialName("discountedAmt")
    val discountedAmt: Float? = null
): Parcelable
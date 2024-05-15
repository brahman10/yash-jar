package com.jar.app.feature_lending.shared.domain.model.temp

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class LoanLimits(
    @SerialName("availableLimit")
    val availableLimit: Float,

    @SerialName("totalLimit")
    val totalLimit: Float,
) : Parcelable
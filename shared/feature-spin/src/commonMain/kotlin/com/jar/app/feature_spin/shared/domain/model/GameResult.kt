package com.jar.app.feature_spin.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class GameResult(
    @SerialName("id") val id: String? = null,
    @SerialName("gameId") val gameId: String? = null,
    @SerialName("outcome") val outcome: Int,
    @SerialName("outcomeType") val outcomeType: String? = null,
    @SerialName("options") val options: List<com.jar.app.feature_spin.shared.domain.model.Option>
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class Option(
    @SerialName("colorCode") val colorCode: String,
    @SerialName("strokeColor") val strokeColor: String,
    @SerialName("value") var value: Int? = null,
    @SerialName("showValue") var showValue: String? = null,
) : Parcelable
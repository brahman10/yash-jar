package com.jar.app.feature_round_off.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class CategoryInfo(
    @SerialName("bgColor")
    val bgColor: String,
    @SerialName("textColor")
    val textColor: String,
    @SerialName("category")
    val categoryName: String,
    @SerialName("icon")
    val iconUrl: String?
): Parcelable

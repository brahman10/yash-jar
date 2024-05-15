package com.jar.app.feature_jar_duo.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class OnboardingData(
    @SerialName("imageUrl")
    val imageUrl: String,

    @SerialName("title")
    val title: String,

    @SerialName("desc")
    val desc: String? = null
) : Parcelable

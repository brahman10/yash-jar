package com.jar.app.feature_spin.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@kotlinx.serialization.Serializable
data class IntroPageModel(
    @SerialName("header") val header: String? = null,
    @SerialName("spinIntroPageDetailsObjects") val spinIntroPageDetailsObjects: List<SpinIntroPageDetail?>? = null,
    @SerialName("playCta") val playCta: PlayCta,
    @SerialName("enabled") val enabled: Boolean? = null,
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class SpinIntroPageDetail(
    @SerialName("index") val index: String? = null,
    @SerialName("iconLink") val iconLink: String? = null,
    @SerialName("firstText") val firstText: String? = null,
    @SerialName("secondText") val secondText: String? = null,
    override val uniqueId: String = index?.plus(iconLink)?.plus(firstText)?.plus(secondText).orEmpty()
) : BaseSpinDataType, Parcelable

interface BaseSpinDataType {
    val uniqueId: String
}


@Parcelize
@kotlinx.serialization.Serializable
data class PlayCta(
    @SerialName("text") val text: String? = null,
    @SerialName("deeplink") val deeplink: String? = null
) : Parcelable

package com.jar.app.feature_spin.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class UseWinningPopupCta(
    @SerialName("popupCta")
    val popupCta: UseWinningModel? = null
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class UseWinningModel(
    @SerialName("ctaDeeplink")
    val ctaDeeplink: String? = null,

    @SerialName("ctaText")
    val ctaText: String? = null,

    @SerialName("title")
    val title: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("url")
    val url: String? = null,

    @SerialName("guideToOpen")
    val guideToOpen: String? = null
) : Parcelable

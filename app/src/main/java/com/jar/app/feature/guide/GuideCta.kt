package com.jar.app.feature.guide

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class GuideCta(
    @SerialName("hasCta")
    val hasCta: Boolean?,

    @SerialName("title")
    val title: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("ctaDeeplink")
    val ctaDeepLink: String? = null,

    @SerialName("ctaText")
    val buttonText: String? = null
) : Parcelable
package com.jar.app.feature_homepage.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class HomeScreenPrompt(
    @SerialName("showBottomSheet")
    val showBottomSheet: Boolean? = null,

    @SerialName("icon")
    val icon: String? = null,

    @SerialName("title")
    val title: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("ctaText")
    val ctaText: String? = null,

    @SerialName("deeplink")
    val deeplink: String? = null,

    @SerialName("featureType")
    val featureType: String? = null,

    @SerialName("timeStamp")
    val timeStamp: String? = null,

    @SerialName("amount")
    val amount: Float? = null
): Parcelable
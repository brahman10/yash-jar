package com.jar.app.feature_coupon_api.domain.model.offer_list


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpinCTA(
    @SerialName("ctaDeeplink")
    val ctaDeeplink: String,
    @SerialName("ctaText")
    val ctaText: String,
    @SerialName("description")
    val description: String?,
    @SerialName("title")
    val title: String,
    @SerialName("url")
    val url: String?
)
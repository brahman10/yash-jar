package com.jar.app.feature.home.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AdSourceData(
    @SerialName("mediaSource")
    val mediaSource: String? = null,

    @SerialName("channel")
    val channel: String? = null,

    @SerialName("adSet")
    val adSet: String? = null,

    @SerialName("campaign")
    val campaign: String? = null,

    @SerialName("agency")
    val agency: String? = null,

    @SerialName("status")
    val status: String? = null,

    @SerialName("installTime")
    val installTime: String? = null,

    @SerialName("afAdType")
    val afAdType: String? = null,

    @SerialName("deeplink")
    val deeplink: String? = null,
)
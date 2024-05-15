package com.jar.app.feature_buy_gold_v2.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AuspiciousTimeResponse(
    @SerialName("isLive")
    val isLive: Boolean? = null,

    @SerialName("validity")
    val validityInSeconds: Int? = null,

    @SerialName("startTime")
    val startTime: String? = null,

    @SerialName("endTime")
    val endTime: String? = null,

    @SerialName("auspiciousTimeId")
    val auspiciousTimeId: String? = null,
)

@kotlinx.serialization.Serializable
data class AuspiciousDateList(
    @SerialName("auspiciousDateList")
    val auspiciousDateList: List<AuspiciousDate>? = null
)

@kotlinx.serialization.Serializable
data class AuspiciousDate(
    @SerialName("date")
    val date: String,

    @SerialName("name")
    val name: String,

    @SerialName("auspiciousTimes")
    val auspiciousTimes: List<AuspiciousTime>
)

@kotlinx.serialization.Serializable
data class AuspiciousTime(
    @SerialName("startTime")
    val startTime: String,

    @SerialName("endTime")
    val endTime: String,

    @SerialName("isActive")
    val isActive: Boolean
)
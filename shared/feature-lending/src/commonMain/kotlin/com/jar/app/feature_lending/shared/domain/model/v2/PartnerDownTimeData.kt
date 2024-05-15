package com.jar.app.feature_lending.shared.domain.model.v2


import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class PartnerDownTimeData(
    @SerialName("countDownTimeInMillis")
    val countDownTimeInMillis: Long? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("isNotificationEnabled")
    val isNotificationEnabled: Boolean? = null,
    @SerialName("notificationDesc")
    val notificationDesc: String? = null,
    @SerialName("title")
    val title: String? = null
)
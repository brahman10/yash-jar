package com.jar.app.feature_in_app_notification.shared.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDTO(
    @SerialName("id")
    val id: String? = null,

    @SerialName("userId")
    val userId: String? = null,

    @SerialName("title")
    val title: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("callToAction")
    val callToAction: String? = null,

    @SerialName("createdAt")
    val createdAt: String? = null,

    @SerialName("createdAtUtc")
    val createdAtUtc: Long? = null,

    @SerialName("icon")
    val icon: String? = null,

    @SerialName("deepLink")
    val deepLink: String? = null,

    @SerialName("seen")
    val seen: Boolean? = null,

    @SerialName("category")
    val category: String? = null
)
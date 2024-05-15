package com.jar.app.feature.notification_list.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class NotificationMetaData(
    @SerialName("notificationCount")
    val notificationCount: Long? = null
)
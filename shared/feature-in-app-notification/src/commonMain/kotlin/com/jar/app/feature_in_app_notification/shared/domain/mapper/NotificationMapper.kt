package com.jar.app.feature_in_app_notification.shared.domain.mapper

import com.jar.app.feature_in_app_notification.shared.data.dto.NotificationDTO
import com.jar.app.feature_in_app_notification.shared.domain.model.Notification

fun NotificationDTO.toNotification(): Notification {
    return Notification(
        id = id,
        userId = userId,
        title = title,
        description = description,
        callToAction = callToAction,
        createdAt = createdAt,
        createdAtUtc = createdAtUtc,
        icon = icon,
        deepLink = deepLink,
        seen = seen,
        category = category
    )
}
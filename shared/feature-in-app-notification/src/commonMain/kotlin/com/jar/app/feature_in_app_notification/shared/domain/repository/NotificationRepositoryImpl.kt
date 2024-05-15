package com.jar.app.feature_in_app_notification.shared.domain.repository

import com.jar.app.feature_in_app_notification.shared.data.network.NotificationDataSource
import com.jar.app.feature_in_app_notification.shared.data.repository.NotificationRepository

internal class NotificationRepositoryImpl (
    private val notificationDataSource: NotificationDataSource) :
    NotificationRepository {

    override suspend fun fetchNotification(
        page: Int,
        size: Int
    ) = notificationDataSource.fetchNotification(page, size)
}
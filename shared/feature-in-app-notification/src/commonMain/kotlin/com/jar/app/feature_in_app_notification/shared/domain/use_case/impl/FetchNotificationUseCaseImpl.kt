package com.jar.app.feature_in_app_notification.shared.domain.use_case.impl

import com.jar.app.feature_in_app_notification.shared.data.repository.NotificationRepository
import com.jar.app.feature_in_app_notification.shared.domain.use_case.FetchNotificationUseCase


internal class FetchNotificationUseCaseImpl constructor(private val notificationRepository: NotificationRepository) :
    FetchNotificationUseCase {

    override suspend fun fetchNotification(
        page: Int,
        size: Int
    ) = notificationRepository.fetchNotification(page, size)

}
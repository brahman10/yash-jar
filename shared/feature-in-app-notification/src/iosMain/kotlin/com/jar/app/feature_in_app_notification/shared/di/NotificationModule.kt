package com.jar.app.feature_in_app_notification.shared.di

import com.jar.app.feature_in_app_notification.shared.data.network.NotificationDataSource
import com.jar.app.feature_in_app_notification.shared.domain.repository.NotificationRepositoryImpl
import com.jar.app.feature_in_app_notification.shared.domain.use_case.FetchNotificationUseCase
import com.jar.app.feature_in_app_notification.shared.domain.use_case.impl.FetchNotificationUseCaseImpl
import io.ktor.client.HttpClient

class NotificationModule(client: HttpClient) {

    private val notificationDataSource by lazy {
        NotificationDataSource(client)
    }

    private val notificationRepository by lazy {
        NotificationRepositoryImpl(notificationDataSource)
    }

    val fetchNotificationUseCase: FetchNotificationUseCase by lazy {
        FetchNotificationUseCaseImpl(notificationRepository)
    }

}
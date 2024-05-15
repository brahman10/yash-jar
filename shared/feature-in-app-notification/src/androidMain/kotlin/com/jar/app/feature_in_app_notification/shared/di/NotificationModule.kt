package com.jar.app.feature_in_app_notification.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_in_app_notification.shared.data.network.NotificationDataSource
import com.jar.app.feature_in_app_notification.shared.data.repository.NotificationRepository
import com.jar.app.feature_in_app_notification.shared.domain.repository.NotificationRepositoryImpl
import com.jar.app.feature_in_app_notification.shared.domain.use_case.FetchNotificationUseCase
import com.jar.app.feature_in_app_notification.shared.domain.use_case.impl.FetchNotificationUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class NotificationModule {

    @Provides
    @Singleton
    internal fun provideNotificationDataSource(@AppHttpClient client: HttpClient): NotificationDataSource {
        return NotificationDataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideNotificationRepository(notificationDataSource: NotificationDataSource): NotificationRepository {
        return NotificationRepositoryImpl(notificationDataSource)
    }

    @Provides
    @Singleton
    internal fun provideFetchNotificationUseCase(notificationRepository: NotificationRepository): FetchNotificationUseCase {
        return FetchNotificationUseCaseImpl(notificationRepository)
    }
}
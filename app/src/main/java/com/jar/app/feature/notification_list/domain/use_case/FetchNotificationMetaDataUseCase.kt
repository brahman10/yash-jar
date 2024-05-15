package com.jar.app.feature.notification_list.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature.notification_list.domain.model.NotificationMetaData
import kotlinx.coroutines.flow.Flow

interface FetchNotificationMetaDataUseCase {
    suspend fun fetchNotificationMetaData(): Flow<RestClientResult<NotificationMetaData>>

    suspend fun updateNotificationMetaData(notificationMetaData: NotificationMetaData)
}
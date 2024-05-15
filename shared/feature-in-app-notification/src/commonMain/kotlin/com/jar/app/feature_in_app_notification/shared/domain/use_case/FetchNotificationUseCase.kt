package com.jar.app.feature_in_app_notification.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_in_app_notification.shared.data.dto.NotificationDTO

interface FetchNotificationUseCase {
    suspend fun fetchNotification(
        page: Int,
        size: Int
    ): RestClientResult<ApiResponseWrapper<List<NotificationDTO>>>
}
package com.jar.app.feature_in_app_notification.shared.data.repository

import com.jar.app.feature_in_app_notification.shared.data.dto.NotificationDTO
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

interface NotificationRepository : BaseRepository {

    suspend fun fetchNotification(
        page: Int,
        size: Int
    ): RestClientResult<ApiResponseWrapper<List<NotificationDTO>>>

}
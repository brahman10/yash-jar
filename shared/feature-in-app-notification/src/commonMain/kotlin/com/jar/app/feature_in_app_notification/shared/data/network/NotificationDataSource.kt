package com.jar.app.feature_in_app_notification.shared.data.network

import com.jar.app.feature_in_app_notification.shared.data.dto.NotificationDTO
import com.jar.app.feature_in_app_notification.shared.util.NotificationConstants.Endpoints
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url

internal class NotificationDataSource(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun fetchNotification(page: Int, size: Int) =
        getResult<ApiResponseWrapper<List<NotificationDTO>>> {
            client.get {
                url(Endpoints.FETCH_NOTIFICATION_LIST)
                parameter("page", page)
                parameter("size", size)
            }
        }
}
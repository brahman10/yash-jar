package com.jar.app.feature_in_app_notification.shared.ui

import com.jar.app.feature_in_app_notification.shared.data.dto.NotificationDTO
import com.jar.app.feature_in_app_notification.shared.domain.mapper.toNotification
import com.jar.app.feature_in_app_notification.shared.domain.use_case.FetchNotificationUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.kuuurt.paging.multiplatform.Pager
import com.kuuurt.paging.multiplatform.PagingConfig
import com.kuuurt.paging.multiplatform.PagingResult
import com.kuuurt.paging.multiplatform.helpers.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEmpty

class NotificationListFragmentViewModel constructor(
    private val fetchNotificationUseCase: FetchNotificationUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _currentRestClientResult =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<List<NotificationDTO>>>>(
            RestClientResult.none()
        )
    val currentRestClientResult: CFlow<RestClientResult<ApiResponseWrapper<List<NotificationDTO>>>>
        get() = _currentRestClientResult.toCommonFlow()

    companion object {
        private const val NETWORK_PAGE_SIZE = 10
    }

    val pager = Pager(
        viewModelScope,
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
            initialLoadSize = NETWORK_PAGE_SIZE
        ),
        initialKey = 0,
        getItems = { currentKey, size ->
            val response = fetchNotificationUseCase.fetchNotification(
                currentKey,
                size
            )
            _currentRestClientResult.emit(response)
            val items = response.data?.data?.map { it.toNotification() }.orEmpty()
            PagingResult(
                items = items,
                currentKey = currentKey,
                prevKey = { currentKey - 1 },
                nextKey = { currentKey + 1 }
            )
        },
    )

    val pagingData = pager.pagingData.cachedIn(viewModelScope).toCommonFlow()
}
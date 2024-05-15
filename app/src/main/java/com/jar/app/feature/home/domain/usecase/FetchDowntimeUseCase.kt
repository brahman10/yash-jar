package com.jar.app.feature.home.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature.home.domain.model.DowntimeResponse
import kotlinx.coroutines.flow.Flow

interface FetchDowntimeUseCase {
    suspend fun fetchDownTime(): Flow<RestClientResult<ApiResponseWrapper<DowntimeResponse?>>>
}
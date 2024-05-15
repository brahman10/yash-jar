package com.jar.app.feature_post_setup.domain.use_case

import com.jar.app.feature_post_setup.domain.model.calendar.CalendarSavingOperations
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchPostSetupSavingOperationsUseCase {
    suspend fun fetchPostSetupSavingOperations(): Flow<RestClientResult<ApiResponseWrapper<CalendarSavingOperations>>>
}
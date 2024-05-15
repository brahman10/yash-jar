package com.jar.app.feature_post_setup.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchPostSetupCalenderDataUseCase {
    suspend fun fetchPostSetupCalendarData(
        startDate: String,
        endDate: String
    ): Flow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_post_setup.domain.model.calendar.CalendarDataResp>>>
}
package com.jar.app.feature.home.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature.home.domain.model.DashboardStaticData
import kotlinx.coroutines.flow.Flow

interface FetchActiveAnalyticsListUseCase {

    suspend fun fetchActiveAnalyticsList(): Flow<RestClientResult<ApiResponseWrapper<DashboardStaticData>>>

}
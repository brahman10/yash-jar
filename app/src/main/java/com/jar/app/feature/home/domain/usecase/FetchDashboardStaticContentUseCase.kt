package com.jar.app.feature.home.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature.home.domain.model.DashboardStaticData
import com.jar.app.core_base.util.BaseConstants.StaticContentType
import kotlinx.coroutines.flow.Flow

interface FetchDashboardStaticContentUseCase {

    suspend fun fetchDashboardStaticContent(staticContentType: StaticContentType): Flow<RestClientResult<ApiResponseWrapper<DashboardStaticData?>>>

}
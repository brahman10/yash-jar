package com.jar.app.feature_lending.shared.domain.use_case

import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.FetchExperianReportRequest
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.SuccessApiResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchExperianReportUseCase {

   suspend fun fetchExperianReportUseCase(fetchExperianReportRequest: FetchExperianReportRequest): Flow<RestClientResult<ApiResponseWrapper<SuccessApiResponse?>>>
}